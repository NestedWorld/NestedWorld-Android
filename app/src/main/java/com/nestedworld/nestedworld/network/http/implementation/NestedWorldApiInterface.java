package com.nestedworld.nestedworld.network.http.implementation;

import com.nestedworld.nestedworld.network.http.models.request.friends.AddFriendRequest;
import com.nestedworld.nestedworld.network.http.models.request.users.auth.ForgotPasswordRequest;
import com.nestedworld.nestedworld.network.http.models.request.users.auth.RegisterRequest;
import com.nestedworld.nestedworld.network.http.models.request.users.auth.SignInRequest;
import com.nestedworld.nestedworld.network.http.models.response.attack.AttacksResponse;
import com.nestedworld.nestedworld.network.http.models.response.friend.AddFriendResponse;
import com.nestedworld.nestedworld.network.http.models.response.monsters.MonsterAttackResponse;
import com.nestedworld.nestedworld.network.http.models.response.monsters.MonstersResponse;
import com.nestedworld.nestedworld.network.http.models.response.object.ObjectDetailResponse;
import com.nestedworld.nestedworld.network.http.models.response.object.ShopObjectsResponse;
import com.nestedworld.nestedworld.network.http.models.response.places.PlacesResponse;
import com.nestedworld.nestedworld.network.http.models.response.places.regions.RegionResponse;
import com.nestedworld.nestedworld.network.http.models.response.places.regions.RegionsResponse;
import com.nestedworld.nestedworld.network.http.models.response.users.UserResponse;
import com.nestedworld.nestedworld.network.http.models.response.users.auth.ForgotPasswordResponse;
import com.nestedworld.nestedworld.network.http.models.response.users.auth.LogoutResponse;
import com.nestedworld.nestedworld.network.http.models.response.users.auth.RegisterResponse;
import com.nestedworld.nestedworld.network.http.models.response.users.auth.SignInResponse;
import com.nestedworld.nestedworld.network.http.models.response.users.friend.FriendsResponse;
import com.nestedworld.nestedworld.network.http.models.response.users.inventory.InventoryResponse;
import com.nestedworld.nestedworld.network.http.models.response.users.monster.UserMonsterResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface NestedWorldApiInterface {
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

    @GET(HttpEndPoint.USER_INVENTORY)
    Call<InventoryResponse> getUserInventory();

    @GET(HttpEndPoint.OBJECT_LIST)
    Call<ShopObjectsResponse> getObjects();

    @GET(HttpEndPoint.OBJECT_DETAIL)
    Call<ObjectDetailResponse> getObjectDetail(@Path("objectId") long objectId);
}