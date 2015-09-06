package com.nestedworld.nestedworld.api.implementation;

import android.content.Context;

import com.nestedworld.nestedworld.api.models.User;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

public class NestedWorldApi {
    private static NestedWorldApi mSingleton;
    private final String TAG = getClass().getSimpleName();
    private Context mContext;
    private ApiInterface mClient;

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

    /*
    ** Singleton
     */
    public static NestedWorldApi getInstance(final Context context) {
        if (mSingleton == null) {
            mSingleton = new NestedWorldApi(context);
        }
        return mSingleton;
    }

    private void init() {
        // Define the interceptor, add authentication headers
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                //TODO completer les header avec les bonnes valeurs
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

    /*
    ** Interface implementation
     */
    public void signUp(final String email, final String password, final Callback<User> callback) {
        //TODO mettre le bon app_token
        mClient.signUp(email, password, "test", callback);
    }

    public void signIn(final String email, final String password, final Callback<User> callback) {
        //TODO mettre le bon app_token
        mClient.signUp(email, password, "test", callback);
    }

    /*
    ** Interface
     */
    public interface ApiInterface {
        @POST(Constant.USER_SIGN_UP)
        @FormUrlEncoded
        void signUp(
                @Field("email") String email,
                @Field("password") String password,
                @Field("app_token") String app_token,
                Callback<User> callback);

        @POST(Constant.USER_SIGN_IN)
        @FormUrlEncoded
        void signIn(
                @Field("email") String email,
                @Field("password") String password,
                @Field("app_token") String app_token,
                Callback<User> callback);
    }
}
