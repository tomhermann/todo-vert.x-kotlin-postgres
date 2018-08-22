package com.fueledbysoda.todo

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class TodoServiceTest {
    private val testObject = TodoService()

    @Test
    fun `Should be able to create task`() {
        val item = testObject.add(TodoItem(title = "Some new task"))

        assertThat(testObject.list().size, equalTo(1))
        assertThat(testObject.list(), contains(item))
    }

    @Test
    fun `Should be able to remove task`() {
        val item = testObject.add(TodoItem(title = "I should be removed"))

        testObject.delete(item.id)

        assertThat(testObject.list().size, equalTo(0))
    }
}
