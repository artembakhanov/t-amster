package com.artembakhanov.hamster.web.main

import com.artembakhanov.hamster.domain.UserInfo

case class MainState(userInfo: UserInfo, tab: Tab)

enum Tab:
    case Tap
    case Mine
