package com.trbear9.plants

import com.trbear9.plants.parameters.UserVariable
import org.apache.commons.csv.CSVRecord

interface Constraint {
    fun evaluate(record: CSVRecord, input: UserVariable): Int
}