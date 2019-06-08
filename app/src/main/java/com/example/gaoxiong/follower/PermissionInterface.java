package com.example.gaoxiong.follower;

/**
 * 权限请求接口
 * Created by gaoxiong on 2018/7/17.
 */

public interface PermissionInterface {
    /**
     * 可设置请求权限请求码
     */
    int getPermissionsRequestCode();

    /**
     * 设置需要请求的权限
     */
    String[] getPermissions();

    /**
     * 请求权限成功回调
     */
    void requestPermissionsSuccess();

    /**
     * 请求权限失败回调
     */
    void requestPermissionsFail();
}
