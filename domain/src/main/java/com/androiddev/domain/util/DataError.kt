package com.androiddev.domain.util

sealed interface DataError {
    enum class Network : DataError {
        SERVER_ERROR,      // 서버 에러 (500 등)
        CONNECTION_ERROR,  // 인터넷 연결 안 됨 (IOException)
        UNEXPECTED_ERROR,  // 알 수 없는 에러
        TOKEN_EXPIRED      // 토큰 만료
    }
}