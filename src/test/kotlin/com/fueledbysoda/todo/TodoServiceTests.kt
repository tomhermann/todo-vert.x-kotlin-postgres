package com.fueledbysoda.todo

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class TodoServiceTests : StringSpec({
    "When created Then no items should exist" {
        val testObject = TodoService()
        testObject.list().size shouldBe 0
    }

    "When added Then one item should exist" {
        val testObject = TodoService()
        val todoItem = TodoItem(title = "test")
        testObject.add(todoItem)
        testObject.list().size shouldBe 1
    }
})
