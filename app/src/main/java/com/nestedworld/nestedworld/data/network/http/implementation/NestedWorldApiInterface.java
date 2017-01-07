package com.nestedworld.nestedworld.data.network.http.implementation;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.data.network.http.models.request.friends.AddFriendRequest;
import com.nestedworld.nestedworld.data.network.http.models.request.users.auth.ForgotPasswordRequest;
import com.nestedworld.nestedworld.data.network.http.models.request.users.auth.RegisterRequest;
import com.nestedworld.nestedworld.data.network.http.models.request.users.auth.SignInRequest;
import com.nestedworld.nestedworld.data.network.http.models.response.attacks.AttacksResponse;
import com.nestedworld.nestedworld.data.network.http.models.response.friends.AddFriendResponse;
import com.nestedworld.nestedworld.data.network.http.models.response.geo.portal.PortalsResponse;
import com.nestedworld.nestedworld.data.network.http.models.response.monsters.MonsterAttackResponse;
import com.nestedworld.nestedworld.data.network.http.models.response.monsters.MonstersResponse;
import com.nestedworld.nestedworld.data.network.http.models.response.objects.ObjectsResponse;
import com.nestedworld.nestedworld.data.network.http.models.response.users.UserDetailResponse;
import com.nestedworld.nestedworld.data.network.http.models.response.users.UserResponse;
import com.nestedworld.nestedworld.data.network.http.models.response.users.UserStatsResponse;
import com.nestedworld.nestedworld.data.network.http.models.response.users.auth.ForgotPasswordResponse;
import com.nestedworld.nestedworld.data.network.http.models.response.users.auth.LogoutResponse;
import com.nestedworld.nestedworld.data.network.http.models.response.users.auth.RegisterResponse;
import com.nestedworld.nestedworld.data.network.http.models.response.users.auth.SignInResponse;
import com.nestedworld.nestedworld.data.network.http.models.response.users.friend.FriendsResponse;
import com.nestedworld.nestedworld.data.network.http.models.response.users.inventory.UserInventoryResponse;
import com.nestedworld.nestedworld.data.network.http.models.response.users.monster.UserMonsterResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface NestedWorldApiInterface {
    @GET(HttpEndPoint.USER_MONSTERS)
    @NonNull
    Call<UserMonsterResponse> getUserMonsters();

    @GET(HttpEndPoint.USER_FRIENDS)
    @NonNull
    Call<FriendsResponse> getFriends();

    @POST(HttpEndPoint.USER_FRIENDS)
    @NonNull
    Call<AddFriendResponse> addFriends(@NonNull @Body final AddFriendRequest body);

    @POST(HttpEndPoint.USER_LOGOUT)
    @NonNull
    Call<LogoutResponse> logout();

    @POST(HttpEndPoint.USER_REGISTER)
    @NonNull
    Call<RegisterResponse> register(@NonNull @Body final RegisterRequest body);

    @POST(HttpEndPoint.USER_SIGN_IN)
    @NonNull
    Call<SignInResponse> signIn(@NonNull @Body final SignInRequest body);

    @POST(HttpEndPoint.USER_PASSWORD)
    @NonNull
    Call<ForgotPasswordResponse> forgotPassword(@NonNull @Body final ForgotPasswordRequest body);

    @GET(HttpEndPoint.MONSTERS_LIST)
    @NonNull
    Call<MonstersResponse> getMonsters();

    @GET(HttpEndPoint.USER_INFO)
    @NonNull
    Call<UserResponse> getUserInfo();

    @GET(HttpEndPoint.ATTACK_LIST)
    @NonNull
    Call<AttacksResponse> getAttacks();

    @GET(HttpEndPoint.MONSTER_ATTACK)
    @NonNull
    Call<MonsterAttackResponse> getMonsterAttack(@Path("monsterId") final long monsterId);

    @GET(HttpEndPoint.USER_INVENTORY)
    @NonNull
    Call<UserInventoryResponse> getUserInventory();

    @GET(HttpEndPoint.OBJECT_LIST)
    @NonNull
    Call<ObjectsResponse> getObjects();

    @GET(HttpEndPoint.PORTALS_LIST)
    @NonNull
    Call<PortalsResponse> getPortals(@Path("latitude") final double latitude,
                                     @Path("longitude") final double longitude);

    @GET(HttpEndPoint.USER_DETAIL)
    @NonNull
    Call<UserDetailResponse> getUserDetail(@Path("userId") final long userId);

    @GET(HttpEndPoint.USER_STATS)
    @NonNull
    Call<UserStatsResponse> getUserStats(@Path("userId") final long userId);
}