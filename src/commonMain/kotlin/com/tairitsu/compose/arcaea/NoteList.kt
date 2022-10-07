package com.tairitsu.compose.arcaea

class NoteList : MutableList<Note> {
    private val data: MutableList<Note> = mutableListOf()

    private val filters: ArrayDeque<NoteFilter> = ArrayDeque()

    private fun applyFilterImpl(note: Note): Note {
        var ret = note

        val filterSize = filters.size
        for (idx in (0 until filterSize).reversed()) {
            ret = filters[idx](ret)
        }

        return ret
    }

    private fun applyFilterImpl(note: Collection<Note>): Collection<Note> {
        return note.map { applyFilterImpl(it) }
    }

    private fun Note.applyFilter(): Note {
        return applyFilterImpl(this)
    }

    private fun Collection<Note>.applyFilter(): Collection<Note> {
        return this.map { applyFilterImpl(it) }
    }

    fun addFilter(filter: NoteFilter) {
        filters.addLast(filter)
    }

    fun popFilter() {
        filters.removeLast()
    }


    override val size: Int
        get() = data.size

    override fun clear() {
        data.clear()
    }

    override fun addAll(elements: Collection<Note>): Boolean {
        return data.addAll(elements.applyFilter())
    }

    override fun addAll(index: Int, elements: Collection<Note>): Boolean {
        return data.addAll(index, elements.applyFilter())
    }

    override fun add(index: Int, element: Note) {
        return data.add(index, element.applyFilter())
    }

    override fun add(element: Note): Boolean {
        return data.add(element.applyFilter())
    }

    override fun get(index: Int): Note {
        return data.get(index)
    }

    override fun isEmpty(): Boolean {
        return data.isEmpty()
    }

    override fun iterator(): MutableIterator<Note> {
        return data.iterator()
    }

    override fun listIterator(): MutableListIterator<Note> {
        return data.listIterator()
    }

    override fun listIterator(index: Int): MutableListIterator<Note> {
        return data.listIterator(index)
    }

    override fun removeAt(index: Int): Note {
        return data.removeAt(index)
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<Note> {
        return data.subList(fromIndex, toIndex)
    }

    override fun set(index: Int, element: Note): Note {
        return data.set(index, element)
    }

    override fun retainAll(elements: Collection<Note>): Boolean {
        return data.retainAll(elements)
    }

    override fun removeAll(elements: Collection<Note>): Boolean {
        return data.removeAll(elements)
    }

    override fun remove(element: Note): Boolean {
        return data.remove(element)
    }

    override fun lastIndexOf(element: Note): Int {
        return data.lastIndexOf(element)
    }

    override fun indexOf(element: Note): Int {
        return data.indexOf(element)
    }

    override fun containsAll(elements: Collection<Note>): Boolean {
        return data.containsAll(elements)
    }

    override fun contains(element: Note): Boolean {
        return data.contains(element)
    }
}

