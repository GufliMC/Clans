package com.guflimc.clans.api.domain;

public enum ClanPermission {
    CHANGE_CREST("clans.permission.change.crest"),
    INVITE_PLAYER("clans.permission.invite.player"),
    KICK_MEMBER("clans.permission.kick.member"),
    ACCESS_STORAGE("clans.permission.access.storage"),
    ACCESS_VAULT("clans.permission.access.vault");

    private final String i18nKey;

    ClanPermission(String i18nKey) {
        this.i18nKey = i18nKey;
    }

    public String i18nKey() {
        return i18nKey;
    }
}
