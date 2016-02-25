package com.nestedworld.nestedworld.api.implementation;

import android.content.Context;
import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.api.models.User;
import com.nestedworld.nestedworld.api.models.apiRequest.users.UpdateUserRequest;
import com.nestedworld.nestedworld.api.models.apiRequest.users.auth.ForgotPasswordRequest;
import com.nestedworld.nestedworld.api.models.apiRequest.users.auth.RegisterRequest;
import com.nestedworld.nestedworld.api.models.apiRequest.users.auth.SignInRequest;
import com.nestedworld.nestedworld.api.models.apiResponse.monsters.MonstersResponse;
import com.nestedworld.nestedworld.api.models.apiResponse.places.PlacesResponse;
import com.nestedworld.nestedworld.api.models.apiResponse.places.regions.RegionsResponse;
import com.nestedworld.nestedworld.api.models.apiResponse.users.UserResponse;
import com.nestedworld.nestedworld.api.models.apiResponse.users.auth.ForgotPasswordResponse;
import com.nestedworld.nestedworld.api.models.apiResponse.users.auth.LogoutResponse;
import com.nestedworld.nestedworld.api.models.apiResponse.users.auth.RegisterResponse;
import com.nestedworld.nestedworld.api.models.apiResponse.users.auth.SignInResponse;
import com.nestedworld.nestedworld.api.models.apiResponse.users.friend.FriendsResponse;
import com.nestedworld.nestedworld.api.models.apiResponse.users.monster.UserMonsterResponse;
import com.nestedworld.nestedworld.authenticator.UserManager;
import com.nestedworld.nestedworld.utils.log.LogHelper;
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
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;

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
    private NestedWorldApi(@NonNull final Context context) {
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
        LogHelper.d(TAG, "Init API (end_point = " + Constant.BASE_END_POINT + ")");

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
    public void register(@NonNull final String email, @NonNull final String password, @NonNull final String pseudo, @NonNull final Callback<RegisterResponse> callback) {
        mClient.register(new RegisterRequest(email, password, pseudo)).enqueue(callback);
    }

    public void signIn(@NonNull final String email, @NonNull final String password, @NonNull final Callback<SignInResponse> callback) {
        String app_token = mContext.getString(R.string.app_token);
        mClient.signIn(new SignInRequest(email, password, app_token)).enqueue(callback);
    }

    public void forgotPassword(@NonNull final String email, @NonNull final Callback<ForgotPasswordResponse> callback) {
        mClient.forgotPassword(new ForgotPasswordRequest(email)).enqueue(callback);
    }

    public void logout(@NonNull final Callback<LogoutResponse> callback) {
        mClient.logout().enqueue(callback);
    }

    public void getMonstersList(@NonNull final Callback<MonstersResponse> callback) {
        mClient.getMonstersList().enqueue(callback);
    }

    public void getUserInfo(@NonNull final Callback<UserResponse> callback) {
        mClient.getUserInfo().enqueue(callback);
    }

    public void getPlaces(@NonNull Callback<PlacesResponse> callback) {
        mClient.getPlaces().enqueue(callback);
    }

    public void getRegionsList(@NonNull final Callback<RegionsResponse> callback) {
        mClient.getRegionsList().enqueue(callback);
    }

    public void getFriendList(@NonNull final Callback<FriendsResponse> callback) {
        mClient.getFriendList().enqueue(callback);
    }

    public void getUserMonster(@NonNull final Callback<UserMonsterResponse> callback) {
        mClient.getUserMonsters().enqueue(callback);
    }

    public void updateUserInformation(@NonNull final UpdateUserRequest userInfo, @NonNull final Callback<UserResponse> callback) {
        mClient.updateUserInfo(userInfo).enqueue(callback);
    }

    /**
     * API Interface which use the retrofit annotation
     */
    public interface ApiInterface {
        @GET(Constant.USER_MONSTERS)
        Call<UserMonsterResponse> getUserMonsters();

        @GET(Constant.USER_FRIENDS)
        Call<FriendsResponse> getFriendList();

        @PUT(Constant.USER_INFO)
        Call<UserResponse> updateUserInfo(@Body UpdateUserRequest body);

        @GET(Constant.USER_LOGOUT)
        Call<LogoutResponse> logout();

        @POST(Constant.USER_REGISTER)
        Call<RegisterResponse> register(@Body RegisterRequest body);

        @POST(Constant.USER_SIGN_IN)
        Call<SignInResponse> signIn(@Body SignInRequest body);

        @POST(Constant.USER_PASSWORD)
        Call<ForgotPasswordResponse> forgotPassword(@Body ForgotPasswordRequest body);

        @GET(Constant.MONSTERS_LIST)
        Call<MonstersResponse> getMonstersList();

        @GET(Constant.USER_INFO)
        Call<UserResponse> getUserInfo();

        @GET(Constant.PLACES_LIST)
        Call<PlacesResponse> getPlaces();

        @GET(Constant.REGIONS_LIST)
        Call<RegionsResponse> getRegionsList();
    }
}
