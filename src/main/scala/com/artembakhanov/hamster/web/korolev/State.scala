package com.artembakhanov.hamster.web.korolev

import com.artembakhanov.hamster.web.main.MainState

enum AppState:
  case NotAuthorized
  case Authorized(level: AuthorizationLevel, state: MainState)
end AppState

enum AuthorizationLevel:
  case User
  case Superuser
end AuthorizationLevel
