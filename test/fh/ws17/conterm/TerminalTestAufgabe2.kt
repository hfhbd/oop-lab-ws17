package fh.ws17.conterm

import org.junit.Assert
import org.junit.Before
import org.junit.Test

/**
 * Tests f�r Aufgabe 2
 *
 *
 * Hinweis: Die Stapelung auf dem Kahn ergibt sich durch die Reihenfolge der
 * Aufrufe von belade und entlade. Die Reihenfolge beim Abladen wird in einem
 * Auftrag eigentlich nicht vorgegeben, ein Auftrag enth�lt einfach die Menge
 * der auf/abzuladenden Container. Damit k�nnen Auftr�ge aber auch
 * "unm�glich sein" (zum Beispiel zwei Container in leichter Bauweise von einem
 * Kahn abholen lassen). Es m�sste somit theoretisch gepr�ft werden, ob ein
 * Auftrag �berhaupt ausf�hrbar ist. Um solche Komplikationen wollen wir uns
 * nicht k�mmern m�ssen, gehen Sie davon aus, dass alle Auftr�ge ausf�hrbar
 * sind.
 */

class TerminalTestAufgabe2 {

    @Before
    fun setUp() {
        /* vor jedem Test die Uhr zur�cksetzen */
        Uhr.reset()
    }

    @Test
    fun testNeuerLastzug() {
        try {
            /* Anlegen zweier Container */
            val cont1 = Container(true, "Luftmatrazen")
            val cont2 = Container(true, "Strandbar")
            val cont3 = Container(true, "Jetski")

            /* Einzige Methode zum Beladen eines Lastzugs ist wieder die
		 * belade-Methode, zum Abladen die entlade-Methode. */
            val lastzug = Lastzug()

            /* Kahn ist anfangs leer */
            Assert.assertEquals(0, lastzug.genutzteKapazitaet)
            Assert.assertEquals(2, lastzug.freieKapazitaet)
            Assert.assertEquals(false, lastzug.enthaelt(cont1.id))
            Assert.assertEquals(false, lastzug.enthaelt(cont2.id))
            Assert.assertEquals(false, lastzug.enthaelt(cont3.id))

            /* mit erstem Container cont1 beladen */
            lastzug.belade(cont1)
            Assert.assertEquals(1, lastzug.genutzteKapazitaet)
            Assert.assertEquals(1, lastzug.freieKapazitaet)
            Assert.assertEquals(true, lastzug.enthaelt(cont1.id))
            Assert.assertEquals(false, lastzug.enthaelt(cont2.id))
            Assert.assertEquals(false, lastzug.enthaelt(cont3.id))

            /* kann man cont2 noch draufpacken? Ja. */
            lastzug.belade(cont2)
            Assert.assertEquals(2, lastzug.genutzteKapazitaet)
            Assert.assertEquals(0, lastzug.freieKapazitaet)
            Assert.assertEquals(true, lastzug.enthaelt(cont1.id))
            Assert.assertEquals(true, lastzug.enthaelt(cont2.id))
            Assert.assertEquals(false, lastzug.enthaelt(cont3.id))

            /* kann man noch mehr aufladen? Nein. */
            lastzug.belade(cont3)
            Assert.assertEquals(2, lastzug.genutzteKapazitaet)
            Assert.assertEquals(0, lastzug.freieKapazitaet)
            Assert.assertEquals(true, lastzug.enthaelt(cont1.id))
            Assert.assertEquals(true, lastzug.enthaelt(cont2.id))
            Assert.assertEquals(false, lastzug.enthaelt(cont3.id))

            /* versuche cont1 zuerst abzuladen, ok */
            var c = lastzug.entlade(cont1.id)
            Assert.assertEquals(cont1, c)
            Assert.assertEquals(1, lastzug.genutzteKapazitaet)
            Assert.assertEquals(1, lastzug.freieKapazitaet)
            Assert.assertEquals(false, lastzug.enthaelt(cont1.id))
            Assert.assertEquals(true, lastzug.enthaelt(cont2.id))
            Assert.assertEquals(false, lastzug.enthaelt(cont3.id))

            /* dann cont2 abladen */
            c = lastzug.entlade(cont2.id)
            Assert.assertEquals(cont2, c)
            Assert.assertEquals(0, lastzug.genutzteKapazitaet)
            Assert.assertEquals(2, lastzug.freieKapazitaet)
            Assert.assertEquals(false, lastzug.enthaelt(cont1.id))
            Assert.assertEquals(false, lastzug.enthaelt(cont2.id))
            Assert.assertEquals(false, lastzug.enthaelt(cont3.id))
        } catch (e: Exception) {
            e.printStackTrace()
            Assert.fail()
        }

    }

    @Test
    fun testNeuerKahn() {
        try {
            /* Anlegen zweier Container */
            val cont1 = Container(true, "Luftmatrazen")
            val cont2 = Container(true, "Strandbar")
            val cont1ID = cont1.id
            val cont2ID = cont2.id

            /*
		 * Einzige Methode zum Beladen eines Kahns ist wieder die
		 * belade-Methode, zum Abladen die entlade-Methode. Die Stapelung auf
		 * dem Kahn ergibt sich durch die Reihenfolge der Aufrufe von belade und
		 * entlade.
		 */
            val kahn = Kahn()

            /* Kahn ist anfangs leer */
            Assert.assertEquals(0, kahn.genutzteKapazitaet)
            Assert.assertEquals(3, kahn.freieKapazitaet)
            Assert.assertEquals(false, kahn.enthaelt(cont1.id))
            Assert.assertEquals(false, kahn.enthaelt(cont2.id))

            /* mit erstem Container cont1 beladen */
            kahn.belade(cont1)
            Assert.assertEquals(1, kahn.genutzteKapazitaet)
            Assert.assertEquals(2, kahn.freieKapazitaet)
            Assert.assertEquals(true, kahn.enthaelt(cont1.id))
            Assert.assertEquals(false, kahn.enthaelt(cont2.id))

            /* kann man cont2 noch draufpacken? Ja. */
            kahn.belade(cont2)
            Assert.assertEquals(2, kahn.genutzteKapazitaet)
            Assert.assertEquals(1, kahn.freieKapazitaet)
            Assert.assertEquals(true, kahn.enthaelt(cont1.id))
            Assert.assertEquals(true, kahn.enthaelt(cont2.id))

            /*
		 * kann man ihn noch mal aufladen? Nein, schon geladen, keine
		 * Ver�nderung.
		 */
            kahn.belade(cont2)
            Assert.assertEquals(2, kahn.genutzteKapazitaet)
            Assert.assertEquals(1, kahn.freieKapazitaet)
            Assert.assertEquals(true, kahn.enthaelt(cont1.id))
            Assert.assertEquals(true, kahn.enthaelt(cont2.id))

            /* versuche cont1 abzuladen - geht nicht, weil er nicht oben liegt */
            var c = kahn.entlade(cont1.id)
            Assert.assertEquals(null, c)
            Assert.assertEquals(2, kahn.genutzteKapazitaet)
            Assert.assertEquals(1, kahn.freieKapazitaet)
            Assert.assertEquals(true, kahn.enthaelt(cont1.id))
            Assert.assertEquals(true, kahn.enthaelt(cont2.id))

            /* erst cont2 abladen - dann geht es */
            c = kahn.entlade(cont2.id)
            Assert.assertEquals(cont2, c)
            Assert.assertEquals(1, kahn.genutzteKapazitaet)
            Assert.assertEquals(2, kahn.freieKapazitaet)
            Assert.assertEquals(true, kahn.enthaelt(cont1.id))
            Assert.assertEquals(false, kahn.enthaelt(cont2.id))

            /* dann cont1 abladen */
            c = kahn.entlade(cont1.id)
            Assert.assertEquals(cont1, c)
            Assert.assertEquals(0, kahn.genutzteKapazitaet)
            Assert.assertEquals(3, kahn.freieKapazitaet)
            Assert.assertEquals(false, kahn.enthaelt(cont1.id))
            Assert.assertEquals(false, kahn.enthaelt(cont2.id))

            // Durch das Be/Entladen haben sich keine Eigenschaften der Container
            // ge�ndert
            Assert.assertEquals("Luftmatrazen", cont1.beschreibung)
            Assert.assertEquals(cont1ID, cont1.id)
            Assert.assertEquals(true, cont1.istStabil())

            Assert.assertEquals("Strandbar", cont2.beschreibung)
            Assert.assertEquals(cont2ID, cont2.id)
            Assert.assertEquals(true, cont2.istStabil())
        } catch (e: Exception) {
            e.printStackTrace()
            Assert.fail()
        }

    }

    @Test
    fun testKahnStapelLS() {

        val kahn = Kahn()
        val cont1 = Container(false, "Luftmatrazen")
        val cont2 = Container(true, "Strandbar")

        /* unten leichte Bausweise, oben stabile Bausweise ist NICHT in Ordnung */
        val ok1 = kahn.belade(cont1)
        val ok2 = kahn.belade(cont2)
        Assert.assertTrue(ok1)
        Assert.assertFalse(ok2)
        Assert.assertEquals(1, kahn.genutzteKapazitaet)
        Assert.assertEquals(2, kahn.freieKapazitaet)
        Assert.assertEquals(true, kahn.enthaelt(cont1.id))
        Assert.assertEquals(false, kahn.enthaelt(cont2.id))

    }

    @Test
    fun testKahnStapelSL() {

        val kahn = Kahn()
        val cont1 = Container(false, "Luftmatrazen")
        val cont2 = Container(true, "Strandbar")

        /* unten stabile Bausweise, oben leichte Bausweise ist in Ordnung */
        val ok1 = kahn.belade(cont2)
        val ok2 = kahn.belade(cont1)
        Assert.assertTrue(ok1)
        Assert.assertTrue(ok2)
        Assert.assertEquals(2, kahn.genutzteKapazitaet)
        Assert.assertEquals(1, kahn.freieKapazitaet)
        Assert.assertEquals(true, kahn.enthaelt(cont1.id))
        Assert.assertEquals(true, kahn.enthaelt(cont2.id))

    }

    @Test
    fun testEinliefern() {
        try { // Dieses Kommando in Abgabe 1+2 ignorieren

            val terminal = Terminal(4, 4)

            val cont1 = Container(false, "Luftmatrazen")
            val cont2 = Container(true, "M�bel")
            val cont3 = Container(true, "Motorboot")

            val kahn = Kahn()
            kahn.belade(cont3)
            kahn.belade(cont2)
            kahn.belade(cont1)

            /*
			 * Beim Kahn haben wir erstmals gestapelte Container bei der
			 * Avisierung. Die Container aus den Avisierungs-Arrays sind
			 * (beginnend bei Index 0) der Reihe nach abzuarbeiten. (Es reicht
			 * eine einfache Einlagerungs-Strategie, Sie m�ssen die Anzahl der
			 * Container-Bewegungen nicht minimieren.)
			 */
            kahn.auftrag = (terminal.avisierung(10,
                    intArrayOf(cont3.id), intArrayOf()))
            Uhr.incZeit(10)
            terminal.abfertigung(kahn)

            /*
			 * Um Container 3 abzuladen, m�ssen beide anderen Container
			 * kurzfristig eingelagert werden.
			 */
            Assert.assertEquals(1, terminal.genutzteKapazitaet)
            Assert.assertEquals(15, terminal.freieKapazitaet)
            val b = terminal.anzahlBewegungen
            Assert.assertTrue(b in 5..6) // 5-6 Bewegungen
            Assert.assertFalse(terminal.enthaelt(cont1.id))
            Assert.assertFalse(terminal.enthaelt(cont2.id))
            Assert.assertTrue(terminal.enthaelt(cont3.id))

            /* Am Ende aber nur ein Container im TerminalController, 2 auf dem Kahn */
            Assert.assertEquals(2, kahn.genutzteKapazitaet)
            Assert.assertEquals(1, kahn.freieKapazitaet)
            Assert.assertTrue(kahn.enthaelt(cont1.id))
            Assert.assertTrue(kahn.enthaelt(cont2.id))
            Assert.assertFalse(kahn.enthaelt(cont3.id))

            /* Geb�hren */
            val fix = 20.0
            val rate = 1.5
            Uhr.incZeit(10)
            Assert.assertEquals(fix + 1 * rate,
                    terminal.getGebuehren(cont1.id), 0.001)
            Assert.assertEquals(fix + 1 * rate,
                    terminal.getGebuehren(cont2.id), 0.001)
            Assert.assertEquals(fix + 11 * rate,
                    terminal.getGebuehren(cont3.id), 0.001)
            Assert.assertEquals(3 * fix + 13 * rate, terminal.gebuehren,
                    0.001)

        } catch (e: Exception) {
            e.printStackTrace()
            Assert.fail()
        }

    }

    /**
     * Hilfsfunktion zum Anliefern dreier Container mit einem Kahn
     */
    private fun anliefernKahn(zeit: Int, t: Terminal, klappt: Boolean): Kahn {
        val kahn = Kahn()
        val cont1 = Container(false, "Luftmatrazen")
        val cont2 = Container(true, "M�bel")
        val cont3 = Container(true, "Motorboot")
        kahn.belade(cont3)
        kahn.belade(cont2)
        kahn.belade(cont1)

        kahn.auftrag = (t.avisierung(zeit,
                intArrayOf(cont1.id, cont2.id, cont3.id), intArrayOf()))

        try { // Dieses Kommando in Abgabe 1+2 ignorieren

            Uhr.incZeit(10)
            val ok = t.abfertigung(kahn)
            Assert.assertEquals(ok, klappt)

        } catch (e: Exception) {
            // stillschweigend ignorieren, Verhalten ab Abgabe 3 anders
        }

        return kahn
    }

    @Test
    fun testeKapazitaet2x2() {

        val t1 = Terminal(2, 2)
        val k1 = anliefernKahn(10, t1, true)
        Assert.assertEquals(3, t1.genutzteKapazitaet)
        Assert.assertEquals(1, t1.freieKapazitaet)
        Assert.assertEquals(3, t1.anzahlBewegungen)
        Assert.assertEquals(3, k1.freieKapazitaet)

        try { // Dieses Kommando in Abgabe 1+2 ignorieren

            /* scheitert, weil nicht genug Kapazit�t vorhanden ist */
            val k2 = anliefernKahn(20, t1, false)
            Assert.assertEquals(0, k2.freieKapazitaet)

        } catch (e: Exception) {
            // ab Abgabe 3 wird die Situation anders behandelt, keine Pr�fung
        }

        Assert.assertEquals(3, t1.genutzteKapazitaet)
        Assert.assertEquals(1, t1.freieKapazitaet)
        Assert.assertEquals(3, t1.anzahlBewegungen)

    }

    @Test
    fun testeKapazitaet4x2() {
        try { // Dieses Kommando in Abgabe 1+2 ignorieren

            val t1 = Terminal(4, 2)
            val k1 = anliefernKahn(10, t1, true)
            Assert.assertEquals(3, t1.genutzteKapazitaet)
            Assert.assertEquals(5, t1.freieKapazitaet)
            Assert.assertEquals(3, t1.anzahlBewegungen)
            Assert.assertEquals(3, k1.freieKapazitaet)
            /* viel Platz, kein Problem */
            val k2 = anliefernKahn(20, t1, true)
            Assert.assertEquals(6, t1.genutzteKapazitaet)
            Assert.assertEquals(2, t1.freieKapazitaet)
            Assert.assertEquals(6, t1.anzahlBewegungen)
            Assert.assertEquals(3, k2.freieKapazitaet)

        } catch (e: Exception) {
            e.printStackTrace()
            Assert.fail()
        }

    }

}