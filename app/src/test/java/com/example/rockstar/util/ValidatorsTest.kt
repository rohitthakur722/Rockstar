package com.example.rockstar.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidatorsTest {

    @Test
    fun `blank full name is rejected`() {
        assertFalse(Validators.isValidFullName(""))
        assertFalse(Validators.isValidFullName("   "))
        assertFalse(Validators.isValidFullName("A"))
    }

    @Test
    fun `trimmed full name of sufficient length is accepted`() {
        assertTrue(Validators.isValidFullName("  Jo  "))
    }

    @Test
    fun `malformed email is rejected`() {
        assertFalse(Validators.isValidEmail(""))
        assertFalse(Validators.isValidEmail("not-an-email"))
        assertFalse(Validators.isValidEmail("missing@domain"))
        assertFalse(Validators.isValidEmail("@nodomain.com"))
    }

    @Test
    fun `well formed email is accepted`() {
        assertTrue(Validators.isValidEmail("user@example.com"))
        assertTrue(Validators.isValidEmail("  user.name+tag@example.co.uk  "))
    }

    @Test
    fun `short password is rejected`() {
        assertFalse(Validators.isValidPassword("12345"))
    }

    @Test
    fun `password meeting minimum length is accepted`() {
        assertTrue(Validators.isValidPassword("123456"))
    }

    @Test
    fun `mismatched password confirmation is rejected`() {
        assertFalse(Validators.doPasswordsMatch("password1", "password2"))
    }

    @Test
    fun `matching password confirmation is accepted`() {
        assertTrue(Validators.doPasswordsMatch("password1", "password1"))
    }

    @Test
    fun `valid registration input passes all checks`() {
        val fullName = "Jane Doe"
        val email = "jane.doe@example.com"
        val password = "securePass123"
        val confirmPassword = "securePass123"

        assertTrue(Validators.isValidFullName(fullName))
        assertTrue(Validators.isValidEmail(email))
        assertTrue(Validators.isValidPassword(password))
        assertTrue(Validators.doPasswordsMatch(password, confirmPassword))
    }
}
