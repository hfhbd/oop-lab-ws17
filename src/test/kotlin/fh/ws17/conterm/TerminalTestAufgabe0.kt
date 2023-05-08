package fh.ws17.conterm

import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.testTimeSource
import kotlin.test.Test
import kotlin.test.assertEquals

class TerminalTestAufgabe0 {

    @Test
    fun testeExistenzTerminal() = runTest {

        // Anlegen eines neuen Terminals mit 4 mal 2 = 8 Container-Stellpl�tzen
        val t = Terminal(4, 2, testTimeSource)

        // richtige Kapazit�t pr�fen
        assertEquals(8, t.freieKapazitaet)
    }
}
