package com.fueledbysoda.todo

import java.util.*
import kotlin.collections.ArrayList

data class TodoItem(
        val id: UUID = UUID.randomUUID(),
        var title: String,
        var completed: Boolean = false,
        val url: String = "${rootUrl()}/$id",
        var order: Int = -1
)

class TodoService {
    private val items = arrayListOf<TodoItem>()

    fun list(): List<TodoItem> {
        return items
    }

    fun clear(): List<TodoItem> {
        items.clear()
        return items
    }

    fun add(input: TodoItem): TodoItem {
        val item = TodoItem(title = input.title, order = input.order)
        items.add(item)
        return item
    }

    fun get(id: UUID): TodoItem? {
        return items.find { it.id == id}
    }

    fun patch(id: UUID, finalState: Map<String, Any>): TodoItem? {
        return get(id)?.let { item ->
            finalState["title"]?.let { item.title = it.toString() }
            finalState["completed"]?.let { item.completed = it.toString().toBoolean() }
            finalState["order"]?.let { item.order = it.toString().toInt() }
            return item
        }
    }

    fun delete(id: UUID): UUID? {
        return get(id)?.let { item ->
            items.remove(item)
            return item.id
        }
    }
}

