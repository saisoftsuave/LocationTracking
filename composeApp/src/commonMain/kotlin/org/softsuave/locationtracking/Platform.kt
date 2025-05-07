package org.softsuave.locationtracking

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform