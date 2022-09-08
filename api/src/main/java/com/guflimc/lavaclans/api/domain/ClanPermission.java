package com.guflimc.lavaclans.api.domain;

public enum ClanPermission {
    CHANGE_NAME("clans.permission.change.name"),
    CHANGE_TAG("clans.permission.change.tag"),
    CHANGE_BANNER("clans.permission.change.banner"),
    CHANGE_COLOR("clans.permission.change.color"),
    INVITE_PLAYER("clans.permission.invite.player"),
    KICK_MEMBER("clans.permission.kick.member"),
    OPEN_VAULT("clans.permission.open.vault"),
    WITHDRAW_MONEY("clans.permission.withdraw.money");

    private final String i18nKey;

    ClanPermission(String i18nKey) {
        this.i18nKey = i18nKey;
    }

    public String i18nKey() {
        return i18nKey;
    }
}
