package com.nestedworld.nestedworld.api.implementation;

import android.content.Context;
import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.api.models.apiResponse.monsters.MonstersList;
import com.nestedworld.nestedworld.api.models.apiResponse.users.User;
import com.nestedworld.nestedworld.api.models.apiResponse.users.auth.ForgotPassword;
import com.nestedworld.nestedworld.api.models.apiResponse.users.auth.Logout;
import com.nestedworld.nestedworld.api.models.apiResponse.users.auth.Register;
import com.nestedworld.nestedworld.api.models.apiResponse.users.auth.SignIn;
import com.nestedworld.nestedworld.authenticator.UserManager;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;

/**
 * Simple Api containing the retrofit interface and his implementation
 */
public class NestedWorldApi {
    private static NestedWorldApi mSingleton;
    private final String TAG = getClass().getSimpleName();
    private ApiInterface mClient;
    private Context mContext;

    /*
    ** Constructor
     */
    public NestedWorldApi(@NonNull final Context context) {
        if (mSingleton != null) {
            return;
        }

        //init API
        init(context);
    }

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
    ** Private method
     */
    private void init(@NonNull final Context context) {
        //set the context to null for avoiding any leaks and then set the new context
        mContext = null;
        mContext = context;

        // Define the interceptor, add authentication headers
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("X-User-Email", UserManager.get(mContext).getCurrentAccountName());
                request.addHeader("Authorization", "Bearer " + UserManager.get(mContext).getCurrentAuthToken(mContext));
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
    public void register(@NonNull final String email, @NonNull final String password, @NonNull final String pseudo, @NonNull final Callback<Register> callback) {
        mClient.register(email, password, pseudo, callback);
    }

    public void signIn(@NonNull final String email, @NonNull final String password, @NonNull final Callback<SignIn> callback) {
        //TODO add the good token (and make it static)
        mClient.signIn(email, password, "test", callback);
    }

    public void forgotPassword(@NonNull final String email, @NonNull final Callback<ForgotPassword> callback) {
        mClient.forgotPassword(email, callback);
    }

    public void logout(@NonNull final Callback<Logout> callback) {
        mClient.logout(callback);
    }

    public void getMonstersList(@NonNull final Callback<MonstersList> callback) {
        mClient.getMonstersList(callback);
    }

    public void getUserInfo(@NonNull Callback<User> callback) {
        mClient.getUserInfo(callback);
    }

    /**
     * API Interface which use the butterknife annotation
     */
    public interface ApiInterface {

        @GET(Constant.USER_LOGOUT)
        @FormUrlEncoded
        void logout(
                Callback<Logout> callback);

        @POST(Constant.USER_REGISTER)
        @FormUrlEncoded
        void register(
                @Field("email") String pseudo,
                @Field("password") String email,
                @Field("pseudo") String password,
                Callback<Register> callback);

        @POST(Constant.USER_SIGN_IN)
        @FormUrlEncoded
        void signIn(
                @Field("email") String email,
                @Field("password") String password,
                @Field("app_token") String app_token,
                Callback<SignIn> callback);

        @POST(Constant.USER_PASSWORD)
        @FormUrlEncoded
        void forgotPassword(
                @Field("email") String email,
                Callback<ForgotPassword> callback);

        @GET(Constant.MONSTERS_LIST)
        void getMonstersList(Callback<MonstersList> callback);

        @GET(Constant.USER_INFO)
        void getUserInfo(Callback<User> callback);
    }
}
