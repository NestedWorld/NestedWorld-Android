package com.nestedworld.nestedworld.api.implementation;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.nestedworld.nestedworld.api.models.apiResponse.monsters.MonstersList;
import com.nestedworld.nestedworld.api.models.apiResponse.users.User;
import com.nestedworld.nestedworld.api.models.apiResponse.users.auth.ForgotPassword;
import com.nestedworld.nestedworld.api.models.apiResponse.users.auth.Logout;
import com.nestedworld.nestedworld.api.models.apiResponse.users.auth.Register;
import com.nestedworld.nestedworld.api.models.apiResponse.users.auth.SignIn;
import com.nestedworld.nestedworld.authenticator.UserManager;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import java.io.IOException;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
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


        // Define a request interceptor for displaying some log
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Define a request interception for adding some custom headers
        Interceptor httpHeaderInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder()
                        .addHeader("X-User-Email", "" + UserManager.get(mContext).getCurrentAccountName())
                        .addHeader("Authorization", "Bearer " + UserManager.get(mContext).getCurrentAuthToken(mContext))
                        .build();
                Log.d(TAG, request.url().toString());
                return chain.proceed(request);
            }
        };

        // We declare the client and we add our interceptors …
        OkHttpClient client = new OkHttpClient();

        client.interceptors().add(httpHeaderInterceptor);

        // we add logging interceptor as last httpHeaderInterceptor
        client.interceptors().add(httpLoggingInterceptor);

        // Init retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.BASE_END_POINT)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        mClient = retrofit.create(ApiInterface.class);
    }

    /**
     * Interface implementation
     */
    public void register(@NonNull final String email, @NonNull final String password, @NonNull final String pseudo, @NonNull final Callback<Register> callback) {
        mClient.register(pseudo, email, password).enqueue(callback);
    }

    public void signIn(@NonNull final String email, @NonNull final String password, @NonNull final Callback<SignIn> callback) {
        //TODO add the good token (and make it static)
        mClient.signIn(email, password, "test").enqueue(callback);
    }

    public void forgotPassword(@NonNull final String email, @NonNull final Callback<ForgotPassword> callback) {
        mClient.forgotPassword(email).enqueue(callback);
    }

    public void logout(@NonNull final Callback<Logout> callback) {
        mClient.logout().enqueue(callback);
    }

    public void getMonstersList(@NonNull final Callback<MonstersList> callback) {
        mClient.getMonstersList().enqueue(callback);
    }

    public void getUserInfo(@NonNull Callback<User> callback) {
        mClient.getUserInfo().enqueue(callback);
    }

    /**
     * API Interface which use the butterknife annotation
     */
    public interface ApiInterface {

        @GET(Constant.USER_LOGOUT)
        Call<Logout> logout();


        @POST(Constant.USER_REGISTER)
        @FormUrlEncoded
        Call<Register> register(
                @Field("email") String pseudo,
                @Field("password") String email,
                @Field("pseudo") String password
        );


        @POST(Constant.USER_SIGN_IN)
        @FormUrlEncoded
        Call<SignIn> signIn(
                @Field("email") String email,
                @Field("password") String password,
                @Field("app_token") String app_token);

        @POST(Constant.USER_PASSWORD)
        @FormUrlEncoded
        Call<ForgotPassword> forgotPassword(
                @Field("email") String email);

        @GET(Constant.MONSTERS_LIST)
        Call<MonstersList> getMonstersList();

        @GET(Constant.USER_INFO)
        Call<User> getUserInfo();
    }
}
