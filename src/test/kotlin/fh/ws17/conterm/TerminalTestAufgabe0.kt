package fh.ws17.conterm

import kotlin.test.*

class TerminalTestAufgabe0 {

    @Test
    fun testeExistenzTerminal() {

        // Anlegen eines neuen Terminals mit 4 mal 2 = 8 Container-Stellpl�tzen
        val t = Terminal(4, 2)

        // richtige Kapazit�t pr�fen
        assertEquals(8, t.freieKapazitaet)
    }
}