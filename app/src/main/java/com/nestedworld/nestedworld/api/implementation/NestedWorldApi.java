package com.nestedworld.nestedworld.api.implementation;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.api.models.apiResponse.monsters.MonstersList;
import com.nestedworld.nestedworld.api.models.apiResponse.users.auth.ForgotPassword;
import com.nestedworld.nestedworld.api.models.apiResponse.users.auth.Logout;
import com.nestedworld.nestedworld.api.models.apiResponse.users.auth.Register;
import com.nestedworld.nestedworld.api.models.apiResponse.users.auth.SignIn;

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

    /*
    ** Constructor
     */
    public NestedWorldApi() {
        if (mSingleton != null) {
            return;
        }

        //init API
        init();
    }

    /*
    ** Singleton
     */
    public static NestedWorldApi getInstance() {
        if (mSingleton == null) {
            mSingleton = new NestedWorldApi();
        }
        return mSingleton;
    }

    /*
    ** Private method
     */
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

    public void logout(@NonNull final String token, @NonNull final Callback<Logout> callback) {
        mClient.logout(token, callback);
    }

    public void getMonstersList(@NonNull final Callback<MonstersList> callback) {
        mClient.getMonstersList(callback);
    }

    /**
     * API Interface which use the butterknife annotation
     */
    public interface ApiInterface {

        @POST(Constant.USER_LOGOUT)
        @FormUrlEncoded
        void logout(
                @Field("app_token") String token,
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
    }
}
