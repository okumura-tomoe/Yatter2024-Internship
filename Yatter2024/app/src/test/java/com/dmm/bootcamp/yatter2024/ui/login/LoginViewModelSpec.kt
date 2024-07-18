package com.dmm.bootcamp.yatter2024.ui.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.dmm.bootcamp.yatter2024.common.MainDispatcherRule
import com.dmm.bootcamp.yatter2024.domain.model.Password
import com.dmm.bootcamp.yatter2024.domain.model.Username
import com.dmm.bootcamp.yatter2024.ui.register.RegisterAccountDestination
import com.dmm.bootcamp.yatter2024.ui.timeline.PublicTimelineDestination
import com.dmm.bootcamp.yatter2024.usecase.login.LoginUseCase
import com.dmm.bootcamp.yatter2024.usecase.login.LoginUseCaseResult
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class LoginViewModelSpec {
  private val loginUseCase = mockk<LoginUseCase>()
  private val subject = LoginViewModel(loginUseCase)

  @get:Rule
  val mainDispatcherRule = MainDispatcherRule()

  @get:Rule
  val rule: TestRule = InstantTaskExecutorRule()

  @Test
  fun changeUsername() = runTest {
    val newUsername = "newUsername"

    subject.onChangedUsername(newUsername)

    assertThat(subject.uiState.value.loginBindingModel.username).isEqualTo(newUsername)
    assertThat(subject.uiState.value.validUsername).isTrue()
  }

  @Test
  fun changePasswordValid() = runTest {
    val newPassword = "newPassword1$"

    subject.onChangedPassword(newPassword)

    assertThat(subject.uiState.value.loginBindingModel.password).isEqualTo(newPassword)
    assertThat(subject.uiState.value.validPassword).isTrue()
  }

  @Test
  fun changePasswordInvalid() = runTest {
    val newPassword = "newPassword"

    subject.onChangedPassword(newPassword)

    assertThat(subject.uiState.value.loginBindingModel.password).isEqualTo(newPassword)
    assertThat(subject.uiState.value.validPassword).isFalse()
  }

  @Test
  fun clickLoginAndNavigatePublicTimeline() = runTest {
    val username = "username"
    val password = "Password1$"

    subject.onChangedUsername(username)
    subject.onChangedPassword(password)

    coEvery {
      loginUseCase.execute(any(), any())
    } returns LoginUseCaseResult.Success

    subject.onClickLogin()

    coVerify {
      loginUseCase.execute(Username(username), Password(password))
    }

    assertThat(subject.destination.value).isInstanceOf(PublicTimelineDestination::class.java)
  }

  @Test
  fun clickLoginAndFailure() = runTest {
    val username = "username"
    val password = "Password1$"

    subject.onChangedUsername(username)
    subject.onChangedPassword(password)

    coEvery {
      loginUseCase.execute(any(), any())
    } returns LoginUseCaseResult.Failure.OtherError(Exception())

    subject.onClickLogin()

    coVerify {
      loginUseCase.execute(Username(username), Password(password))
    }

    assertThat(subject.destination.value).isNull()
  }

  @Test
  fun clickRegisterAndNavigate() = runTest {
    subject.onClickRegister()

    assertThat(subject.destination.value).isInstanceOf(RegisterAccountDestination::class.java)
  }
}