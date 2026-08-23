package com.trbear9.plants

import org.apache.commons.csv.CSVRecord

interface Constraint {
    fun evaluate(record: CSVRecord): Int
}