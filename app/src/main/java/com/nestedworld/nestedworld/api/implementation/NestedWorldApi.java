package com.nestedworld.nestedworld.api.implementation;

import android.content.Context;
import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.api.models.User;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Simple Api containing the retrofit interface and his implementation
 */
public class NestedWorldApi {
    private static NestedWorldApi mSingleton;
    private final String TAG = getClass().getSimpleName();
    private Context mContext;
    private ApiInterface mClient;

    /*
    ** Singleton
     */
    public static NestedWorldApi getInstance(@NonNull final Context context) {
        if (mSingleton == null) {
            mSingleton = new NestedWorldApi(context);
        }
        return mSingleton;
    }

    /*
    ** Constructor
     */
    public NestedWorldApi(final Context context) {
        if (mSingleton != null) {
            return;
        }

        //init API
        mContext = context;
        init();
    }


    private void init() {
        // Define the interceptor, add authentication headers
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                //TODO complete header with real value
                request.addHeader("X-User-Id", "userId");
                request.addHeader("X-User-Email", "userEmail");
                request.addHeader("X-User-Token", "authenticationToken");
                request.addHeader("X-User-Push-Token", "pushToken");
            }
        };

        // Add request interceptor when building adapter
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constant.BASE_END_POINT)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setRequestInterceptor(requestInterceptor)
                .build();
        mClient = restAdapter.create(ApiInterface.class);
    }

    /**
     * Interface implementation
     */
    public void signUp(@NonNull final String email, @NonNull final String password, @NonNull final Callback<User> callback) {
        mClient.signUp(email, password, callback);
    }

    public void signIn(@NonNull final String email, @NonNull final String password, @NonNull final Callback<User> callback) {
        //TODO add the good token (and make it static)
        mClient.signIn(email, password, "test", callback);
    }

    public void forgotPassword(@NonNull final String email, @NonNull final Callback<User> callback) {
        mClient.forgotPassword(email, callback);
    }

    /**
     * API Interface which use the butterknife annotation
     */
    public interface ApiInterface {
        @POST(Constant.USER_SIGN_UP)
        @FormUrlEncoded
        void signUp(
                @Field("email") String email,
                @Field("password") String password,
                Callback<User> callback);

        @POST(Constant.USER_SIGN_IN)
        @FormUrlEncoded
        void signIn(
                @Field("email") String email,
                @Field("password") String password,
                @Field("app_token") String app_token,
                Callback<User> callback);

        @POST(Constant.USER_PASSWORD)
        @FormUrlEncoded
        void forgotPassword(
                @Field("email") String email,
                Callback<User> callback);
    }
}
