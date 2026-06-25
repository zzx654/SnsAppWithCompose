package com.androiddev.domain.exception

sealed class AppException : Exception()

class TokenExpiredException : AppException()

class ConnectionException : AppException()

class ServerException() : AppException()

class UnknownException : AppException()