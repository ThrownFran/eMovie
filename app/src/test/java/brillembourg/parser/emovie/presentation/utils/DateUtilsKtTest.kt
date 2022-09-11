package brillembourg.parser.emovie.presentation.utils

import brillembourg.parser.emovie.data.utils.extractDateFromString
import brillembourg.parser.emovie.data.utils.extractYearFromDate
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class DateUtilsKtTest {

    @Test
    fun `given yyyy-mm-dd format then return year as Int`() {
        //Arrange
        val releaseDate = "1993-11-05"
        //Act
        val year = extractYearFromDate(releaseDate)
        //Assert
        assertEquals(1993, year)
    }

    @Test
    fun `given yy-mm-dd format then return year as Int`() {
        //Arrange
        val releaseDate = "1993-11-05"
        //Act
        val year = extractYearFromDate(releaseDate)
        //Assert
        assertEquals(1993, year)
    }

    @Test
    fun `given yy-mm-dd, then return Date object`() {
        //Arrange
        val releaseDate = "2000-11-05"
        //Act
        val date = extractDateFromString(releaseDate)
        //Assert
        assertNotNull(date)
        assertEquals(2000,date.year)
        assertEquals(11,date.monthValue)
        assertEquals(5,date.dayOfMonth)
    }

    @Test
    fun `given date, then return year as Int`() {
        //Arrange
        val releaseDate = "1993-11-05"
        //Act
        val year = extractYearFromDate(releaseDate)
        //Assert
        assertEquals(1993, year)
    }


}