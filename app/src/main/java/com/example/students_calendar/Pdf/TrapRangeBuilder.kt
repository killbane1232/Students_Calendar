package com.example.students_calendar.Pdf

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.Range
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList


class TrapRangeBuilder {
    private val ranges: MutableList<Range<Int>> = ArrayList()
    fun addRange(range: Range<Int>): TrapRangeBuilder {
        ranges.add(range)
        return this
    }

    /**
     * The result will be ordered by lowerEndpoint ASC
     *
     * @return
     */
    fun build(): List<Range<Int>> {
        val retVal: MutableList<Range<Int>> = ArrayList()
        //order range by lower Bound
        Collections.sort(ranges,
            Comparator<Range<Int>> { o1, o2 -> o1.lowerEndpoint().compareTo(o2.lowerEndpoint()) })
        for (range in ranges) {
            if (retVal.isEmpty()) {
                retVal.add(range)
            } else {
                val lastRange: Range<Int> = retVal[retVal.size - 1]
                if (lastRange.isConnected(range)) {
                    val newLastRange = lastRange.span(range)
                    retVal[retVal.size - 1] = newLastRange
                } else {
                    retVal.add(range)
                }
            }
        }
        //debug
        //return
        return retVal
    }
}