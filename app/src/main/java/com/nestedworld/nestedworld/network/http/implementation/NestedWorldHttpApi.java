package com.nestedworld.nestedworld.network.http.implementation;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.GsonBuilder;
import com.nestedworld.nestedworld.database.models.Region;
import com.nestedworld.nestedworld.database.models.Session;
import com.nestedworld.nestedworld.helpers.log.LogHelper;
import com.nestedworld.nestedworld.helpers.session.SessionHelper;
import com.nestedworld.nestedworld.network.http.models.request.friends.AddFriendRequest;
import com.nestedworld.nestedworld.network.http.models.request.users.auth.ForgotPasswordRequest;
import com.nestedworld.nestedworld.network.http.models.request.users.auth.RegisterRequest;
import com.nestedworld.nestedworld.network.http.models.request.users.auth.SignInRequest;
import com.nestedworld.nestedworld.network.http.models.response.attack.AttacksResponse;
import com.nestedworld.nestedworld.network.http.models.response.friend.AddFriendResponse;
import com.nestedworld.nestedworld.network.http.models.response.monsters.MonsterAttackResponse;
import com.nestedworld.nestedworld.network.http.models.response.monsters.MonstersResponse;
import com.nestedworld.nestedworld.network.http.models.response.places.PlacesResponse;
import com.nestedworld.nestedworld.network.http.models.response.places.regions.RegionResponse;
import com.nestedworld.nestedworld.network.http.models.response.places.regions.RegionsResponse;
import com.nestedworld.nestedworld.network.http.models.response.users.UserResponse;
import com.nestedworld.nestedworld.network.http.models.response.users.auth.ForgotPasswordResponse;
import com.nestedworld.nestedworld.network.http.models.response.users.auth.LogoutResponse;
import com.nestedworld.nestedworld.network.http.models.response.users.auth.RegisterResponse;
import com.nestedworld.nestedworld.network.http.models.response.users.auth.SignInResponse;
import com.nestedworld.nestedworld.network.http.models.response.users.friend.FriendsResponse;
import com.nestedworld.nestedworld.network.http.models.response.users.monster.UserMonsterResponse;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Url;

/**
 * Simple Api containing the retrofit interface and his implementation
 */
public final class NestedWorldHttpApi {
    private static NestedWorldHttpApi mSingleton;
    private final String TAG = getClass().getSimpleName();
    private ApiInterface mClient;

    /*
    ** Constructor
     */
    private NestedWorldHttpApi() {
        if (mSingleton != null) {
            return;
        }

        //init API
        init();
    }

    /*
    ** Singleton
     */
    public static NestedWorldHttpApi getInstance(@NonNull final Context context) {
        if (mSingleton == null) {
            mSingleton = new NestedWorldHttpApi();
        }
        return mSingleton;
    }

    /*
    ** Avoid leek when log-out
     */
    public static void reset() {
        mSingleton = null;
    }

    /*
    ** Private method
     */
    private void init() {
        LogHelper.d(TAG, "Init API(http) (end_point = " + HttpEndPoint.BASE_END_POINT + ")");

        // Define a request interceptor for displaying some log
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Define a request interception for adding some custom headers
        Interceptor httpHeaderInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                Request.Builder requestBuilder = chain.request().newBuilder();

                Session session = SessionHelper.getSession();
                if (session != null) {
                    requestBuilder.addHeader("X-User-Email", session.email);
                    requestBuilder.addHeader("Authorization", session.authToken);
                }

                return chain.proceed(requestBuilder.build());
            }
        };

        // We declare the client and we add our interceptors …
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(httpHeaderInterceptor)
                .addInterceptor(httpLoggingInterceptor)// we add logging interceptor as last httpHeaderInterceptor
                .build();

        // Create the converterFactory
        GsonBuilder builder = new GsonBuilder();
        builder.excludeFieldsWithoutExposeAnnotation();
        GsonConverterFactory gsonConverterFactory = GsonConverterFactory.create(builder.create());

        // Init retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HttpEndPoint.BASE_END_POINT)
                .addConverterFactory(gsonConverterFactory)
                .client(client)
                .build();

        mClient = retrofit.create(ApiInterface.class);
    }

    /**
     * Interface implementation
     */
    public Call<RegisterResponse> register(@NonNull final String email, @NonNull final String password, @NonNull final String pseudo) {
        return mClient.register(new RegisterRequest(email, password, pseudo));
    }

    public Call<SignInResponse> signIn(@NonNull final String email, @NonNull final String password) {
        String appToken = "test";
        return mClient.signIn(new SignInRequest(email, password, appToken));
    }

    public Call<ForgotPasswordResponse> forgotPassword(@NonNull final String email) {
        return mClient.forgotPassword(new ForgotPasswordRequest(email));
    }

    public Call<AddFriendResponse> addFriend(@NonNull final String pseudo) {
        return mClient.addFriends(new AddFriendRequest(pseudo));
    }

    public Call<MonsterAttackResponse> getMonsterAttack(@NonNull final long monsterId) {
        return mClient.getMonsterAttack(monsterId);
    }

    public Call<AttacksResponse> getAttacks() {
        return mClient.getAttacks();
    }

    public Call<LogoutResponse> logout() {
        return mClient.logout();
    }

    public Call<MonstersResponse> getMonsters() {
        return mClient.getMonsters();
    }

    public Call<UserResponse> getUserInfo() {
        return mClient.getUserInfo();
    }

    public Call<PlacesResponse> getPlaces() {
        return mClient.getPlaces();
    }

    public Call<RegionsResponse> getRegions() {
        return mClient.getRegions();
    }

    public Call<RegionResponse> getRegionDetails(@NonNull final Region region) {
        return mClient.getRegionDetail(region.url);
    }

    public Call<FriendsResponse> getFriends() {
        return mClient.getFriends();
    }

    public Call<UserMonsterResponse> getUserMonster() {
        return mClient.getUserMonsters();
    }


    /**
     * API Interface which use the retrofit annotation
     */
    public interface ApiInterface {
        @GET(HttpEndPoint.USER_MONSTERS)
        Call<UserMonsterResponse> getUserMonsters();

        @GET(HttpEndPoint.USER_FRIENDS)
        Call<FriendsResponse> getFriends();

        @POST(HttpEndPoint.USER_FRIENDS)
        Call<AddFriendResponse> addFriends(@Body AddFriendRequest body);

        @POST(HttpEndPoint.USER_LOGOUT)
        Call<LogoutResponse> logout();

        @POST(HttpEndPoint.USER_REGISTER)
        Call<RegisterResponse> register(@Body RegisterRequest body);

        @POST(HttpEndPoint.USER_SIGN_IN)
        Call<SignInResponse> signIn(@Body SignInRequest body);

        @POST(HttpEndPoint.USER_PASSWORD)
        Call<ForgotPasswordResponse> forgotPassword(@Body ForgotPasswordRequest body);

        @GET(HttpEndPoint.MONSTERS_LIST)
        Call<MonstersResponse> getMonsters();

        @GET(HttpEndPoint.USER_INFO)
        Call<UserResponse> getUserInfo();

        @GET(HttpEndPoint.PLACES_LIST)
        Call<PlacesResponse> getPlaces();

        @GET(HttpEndPoint.REGIONS_LIST)
        Call<RegionsResponse> getRegions();

        @GET
        Call<RegionResponse> getRegionDetail(@Url String endPoint);

        @GET(HttpEndPoint.ATTACK_LIST)
        Call<AttacksResponse> getAttacks();

        @GET(HttpEndPoint.MONSTER_ATTACK)
        Call<MonsterAttackResponse> getMonsterAttack(@Path("monsterId") long monsterId);
    }
}
