package com.example.instagramclone.services;

import com.example.instagramclone.api.models.SubscriptionResponse;
import com.example.instagramclone.dao.SubscriptionDAO;
import com.example.instagramclone.dao.UserDAO;
import com.example.instagramclone.models.LocalUser;
import com.example.instagramclone.models.Subscription;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SubscriptionService {
    private final SubscriptionDAO subscriptionDAO;
    private final UserDAO userDAO;
    private final ModelMapper modelMapper;

    public SubscriptionService(SubscriptionDAO subscriptionDAO, UserDAO userDAO, ModelMapper modelMapper) {
        this.subscriptionDAO = subscriptionDAO;
        this.userDAO = userDAO;
        this.modelMapper = modelMapper;
    }

    public void follow(LocalUser localUser, LocalUser followingUser) {
        if (subscriptionDAO.findByLocalUserAndFollowingUser(localUser, followingUser).isPresent())
            return;

        Subscription subscription = new Subscription();
        subscription.setLocalUser(localUser);
        subscription.setFollowingUser(followingUser);

        localUser.setFollowing(localUser.getFollowing() + 1);
        followingUser.setFollowers(followingUser.getFollowers() + 1);

        subscriptionDAO.save(subscription);
        userDAO.save(localUser);
        userDAO.save(followingUser);
    }

    public void unfollow(LocalUser localUser, LocalUser followingUser) {
        Optional<Subscription> opSub = subscriptionDAO.findByLocalUserAndFollowingUser(localUser, followingUser);

        if (opSub.isPresent()) {
            localUser.setFollowing(localUser.getFollowing() - 1);
            followingUser.setFollowers(followingUser.getFollowers() - 1);
            subscriptionDAO.delete(opSub.get());
        }
    }

    public List<SubscriptionResponse> getFollowings(LocalUser user) {
        List <Subscription> subscriptions = subscriptionDAO.findByLocalUserOrderByIdDesc(user);
        List<SubscriptionResponse> subscriptionResponses = new ArrayList<>();

        for (Subscription subscription: subscriptions) {
           subscriptionResponses.add(new SubscriptionResponse(subscription.getFollowingUser().getUsername(), subscription.getFollowingUser().getBio()));
        }

        return subscriptionResponses;
    }

    public List<SubscriptionResponse> getFollowers(LocalUser user) {
        List<Subscription> subscriptions = subscriptionDAO.findByFollowingUserOrderByIdDesc(user);
        List<SubscriptionResponse> subscriptionResponses = new ArrayList<>();

        for (Subscription subscription: subscriptions) {
            subscriptionResponses.add(new SubscriptionResponse(subscription.getLocalUser().getUsername(), subscription.getLocalUser().getBio()));
        }

        return subscriptionResponses;
    }

    public List<SubscriptionResponse> getMutualFollowers(LocalUser user1, LocalUser user2) {
        List<Subscription> user1Sub = subscriptionDAO.findByFollowingUserOrderByIdDesc(user1);
        List<Subscription> user2Sub = subscriptionDAO.findByFollowingUserOrderByIdDesc(user2);
        List<Subscription> mutual = new ArrayList<>();
        Map<LocalUser, Subscription> map = new HashMap<>();

        for (Subscription sub : user1Sub) {
            map.put(sub.getLocalUser(), sub);
        }

        for (Subscription sub : user2Sub) {
            if (map.containsKey(sub.getLocalUser())) {
                mutual.add(sub);
            }
        }

        List<SubscriptionResponse> response = new ArrayList<>();

        for (Subscription subscription: mutual) {
            response.add(new SubscriptionResponse(subscription.getLocalUser().getUsername(), subscription.getLocalUser().getBio()));
        }

        return response;
    }
}