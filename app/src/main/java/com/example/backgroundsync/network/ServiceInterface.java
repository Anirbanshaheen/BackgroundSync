package com.example.backgroundsync.network;

import com.example.backgroundsync.model.Post;
import com.example.backgroundsync.model.PostModel;
import com.example.backgroundsync.model.PostResponse;
import com.example.backgroundsync.utils.ConstantField;

import java.util.List;
import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ServiceInterface {

    @GET(ConstantField.POST)
    Single<List<PostModel>> getPosts();

    @POST(ConstantField.POST)
    Single<PostResponse> postData(@Body Post post);
 }
