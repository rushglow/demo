package example.micronaut

import com.example.model.dto.UserDto
import com.example.model.entity.UserEntity
import com.example.repository.UserRepository
import com.example.security.PasswordEncoderProvider
import com.example.service.TokenService
import com.example.service.UserService
import io.micronaut.http.HttpRequest
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.authentication.AuthenticationFailureReason
import io.micronaut.security.authentication.AuthenticationRequest
import io.micronaut.security.authentication.AuthenticationResponse
import io.micronaut.security.authentication.provider.HttpRequestAuthenticationProvider
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Singleton
class AuthenticationProviderUserPassword<B> : HttpRequestAuthenticationProvider<B> {

    @Inject
    lateinit var userService: UserService

    @Inject
    lateinit var tokenService: TokenService

    @Inject
    lateinit var passwordEncoderProvider: PasswordEncoderProvider

    override fun authenticate(
        httpRequest: HttpRequest<B>?,
        authenticationRequest: AuthenticationRequest<String, String>
    ): AuthenticationResponse {
        try {
            var user: UserDto = userService.getUserByUsername(authenticationRequest.identity)
            if(passwordEncoderProvider.matches(authenticationRequest.secret.toString(), user.password!!)){
                return AuthenticationResponse.success(authenticationRequest.identity, listOf("ROLE_USER"))
            } else {
                return AuthenticationResponse.failure(AuthenticationFailureReason.CREDENTIALS_DO_NOT_MATCH)
            }
        } catch (exception : RuntimeException) {
            return AuthenticationResponse.failure(AuthenticationFailureReason.USER_NOT_FOUND)
        }
    }
}