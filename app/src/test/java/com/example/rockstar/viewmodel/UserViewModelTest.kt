package com.example.rockstar.viewmodel

import com.example.rockstar.model.User
import com.example.rockstar.repo.FakeUserRepo
import com.google.firebase.FirebaseNetworkException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var repo: FakeUserRepo
    private lateinit var viewModel: UserViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        repo = FakeUserRepo()
        viewModel = UserViewModel(repo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login success updates authenticated state and emits navigation event`() = runTest(dispatcher) {
        val user = User(uid = "uid1", fullName = "Jane Doe", email = "jane@example.com")
        repo.loginResult = Result.success(user)
        val events = mutableListOf<AuthEvent>()
        val job = launch { viewModel.events.collect { events.add(it) } }

        viewModel.login("jane@example.com", "password1")
        advanceUntilIdle()

        val state = viewModel.authState.value
        assertTrue(state.isAuthenticated)
        assertFalse(state.isLoading)
        assertEquals(user, state.currentUser)
        assertTrue(events.any { it is AuthEvent.NavigateToHome })
        job.cancel()
    }

    @Test
    fun `login failure exposes a professional error and clears loading`() = runTest(dispatcher) {
        repo.loginResult = Result.failure(FirebaseNetworkException("network down"))
        val events = mutableListOf<AuthEvent>()
        val job = launch { viewModel.events.collect { events.add(it) } }

        viewModel.login("jane@example.com", "wrongpassword")
        advanceUntilIdle()

        val state = viewModel.authState.value
        assertFalse(state.isAuthenticated)
        assertFalse(state.isLoading)
        val errorEvent = events.filterIsInstance<AuthEvent.ShowError>().firstOrNull()
        assertNotNull(errorEvent)
        assertFalse(errorEvent!!.message.contains("network down"))
        job.cancel()
    }

    @Test
    fun `registration success updates user state and emits navigation event`() = runTest(dispatcher) {
        val user = User(uid = "uid2", fullName = "New User", email = "new@example.com")
        repo.registerResult = Result.success(user)
        val events = mutableListOf<AuthEvent>()
        val job = launch { viewModel.events.collect { events.add(it) } }

        viewModel.register("New User", "new@example.com", "password1")
        advanceUntilIdle()

        val state = viewModel.authState.value
        assertTrue(state.isAuthenticated)
        assertEquals(user, state.currentUser)
        assertTrue(events.any { it is AuthEvent.NavigateToHome })
        job.cancel()
    }

    @Test
    fun `registration failure clears loading state without authenticating`() = runTest(dispatcher) {
        repo.registerResult = Result.failure(IllegalStateException("profile write failed"))
        val events = mutableListOf<AuthEvent>()
        val job = launch { viewModel.events.collect { events.add(it) } }

        viewModel.register("New User", "new@example.com", "password1")
        advanceUntilIdle()

        val state = viewModel.authState.value
        assertFalse(state.isLoading)
        assertFalse(state.isAuthenticated)
        assertTrue(events.any { it is AuthEvent.ShowError })
        job.cancel()
    }

    @Test
    fun `password reset success emits confirmation message`() = runTest(dispatcher) {
        repo.resetPasswordResult = Result.success(Unit)
        val events = mutableListOf<AuthEvent>()
        val job = launch { viewModel.events.collect { events.add(it) } }

        viewModel.sendPasswordResetEmail("jane@example.com")
        advanceUntilIdle()

        assertTrue(events.any { it is AuthEvent.ShowMessage })
        assertFalse(viewModel.authState.value.isLoading)
        job.cancel()
    }

    @Test
    fun `password reset failure exposes an error`() = runTest(dispatcher) {
        repo.resetPasswordResult = Result.failure(FirebaseNetworkException("offline"))
        val events = mutableListOf<AuthEvent>()
        val job = launch { viewModel.events.collect { events.add(it) } }

        viewModel.sendPasswordResetEmail("jane@example.com")
        advanceUntilIdle()

        assertTrue(events.any { it is AuthEvent.ShowError })
        assertFalse(viewModel.authState.value.isLoading)
        job.cancel()
    }

    @Test
    fun `logout clears authenticated state and emits navigation event`() = runTest(dispatcher) {
        val user = User(uid = "uid1", fullName = "Jane Doe", email = "jane@example.com")
        repo.loginResult = Result.success(user)
        viewModel.login("jane@example.com", "password1")
        advanceUntilIdle()
        assertTrue(viewModel.authState.value.isAuthenticated)

        val events = mutableListOf<AuthEvent>()
        val job = launch { viewModel.events.collect { events.add(it) } }

        viewModel.logout()
        advanceUntilIdle()

        val state = viewModel.authState.value
        assertFalse(state.isAuthenticated)
        assertNull(state.currentUser)
        assertTrue(repo.logoutCalled)
        assertTrue(events.any { it is AuthEvent.NavigateToLogin })
        job.cancel()
    }

    @Test
    fun `profile update success refreshes current user`() = runTest(dispatcher) {
        val originalUser = User(uid = "uid1", fullName = "Jane", email = "jane@example.com")
        repo.loginResult = Result.success(originalUser)
        viewModel.login("jane@example.com", "password1")
        advanceUntilIdle()

        val updatedUser = originalUser.copy(fullName = "Jane Updated", updatedAt = 123L)
        repo.updateProfileResult = Result.success(updatedUser)
        val events = mutableListOf<AuthEvent>()
        val job = launch { viewModel.events.collect { events.add(it) } }

        viewModel.updateProfile("Jane Updated")
        advanceUntilIdle()

        assertEquals(updatedUser, viewModel.authState.value.currentUser)
        assertFalse(viewModel.profileState.value.isSaving)
        assertTrue(events.any { it is AuthEvent.ShowMessage })
        job.cancel()
    }

    @Test
    fun `duplicate login submission is prevented while a request is in flight`() = runTest(dispatcher) {
        repo.loginResult = Result.success(User(uid = "uid1", email = "jane@example.com"))

        viewModel.login("jane@example.com", "password1")
        viewModel.login("jane@example.com", "password1")
        advanceUntilIdle()

        assertEquals(1, repo.loginCallCount)
    }
}
