package fh.ws17.conterm

import org.junit.Assert
import org.junit.Test

class TerminalTestAufgabe0 {

    @Test
    fun testeExistenzTerminal() {

        // Anlegen eines neuen Terminals mit 4 mal 2 = 8 Container-Stellpl�tzen
        val t = Terminal(4, 2)

        // richtige Kapazit�t pr�fen
        Assert.assertEquals(8, t.freieKapazitaet)
    }
}