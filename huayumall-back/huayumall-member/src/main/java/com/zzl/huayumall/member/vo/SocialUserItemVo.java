package com.zzl.huayumall.member.vo;

import lombok.Data;

import java.util.Date;

/**
 * 功能描述
 *  gitee用户详细信息
 * @author 郑子浪
 * @date 2022/08/17  13:43
 */
@Data
public class SocialUserItemVo {
    private long id;
    private String login;
    private String name;
    private String avatarUrl;
    private String url;
    private String htmlUrl;
    private String remark;
    private String followersUrl;
    private String followingUrl;
    private String gistsUrl;
    private String starredUrl;
    private String subscriptionsUrl;
    private String organizationsUrl;
    private String reposUrl;
    private String eventsUrl;
    private String receivedEventsUrl;
    private String type;
    private String blog;
    private String weibo;
    private String bio;
    private int publicRepos;
    private int publicGists;
    private int followers;
    private int following;
    private int stared;
    private int watched;
    private Date createdAt;
    private Date updatedAt;
    private String email;
}
