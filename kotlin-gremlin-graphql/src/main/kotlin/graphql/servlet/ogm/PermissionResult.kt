package graphql.servlet.ogm

sealed class PermissionResult {
    object Allow : PermissionResult()
    data class Deny(val reason: String) : PermissionResult()
}
