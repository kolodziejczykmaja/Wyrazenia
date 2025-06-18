package wyrażeniaZUpraszczaniem;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Kompletny zestaw testów JUnit dla systemu wyrażeń matematycznych
 * z upraszczaniem. Testuje wszystkie klasy i funkcjonalności.
 */
@DisplayName("Testy systemu wyrażeń matematycznych")
public class WyrażenieTestSuite {

	private Zmienna x;
	private Zero zero;
	private Jeden jeden;
	private Stała dwa, trzy, minusJeden;

	@BeforeEach
	void setUp() {
		x = Zmienna.twórz();
		zero = Zero.twórz();
		jeden = Jeden.twórz();
		dwa = Stała.twórz(2.0);
		trzy = Stała.twórz(3.0);
		minusJeden = Stała.twórz(-1.0);
	}

	// ========== TESTY STAŁYCH ==========

	@Nested
	@DisplayName("Testy klas stałych")
	class StałeTest {

		@Test
		@DisplayName("Test singletona Zero")
		void testZeroSingleton() {
			Zero zero1 = Zero.twórz();
			Zero zero2 = Zero.twórz();
			assertSame(zero1, zero2, "Zero powinno być singletonem");
			assertEquals(0.0, zero1.policz(5.0), 0.001);
			assertEquals("0.0", zero1.toString());
		}

		@Test
		@DisplayName("Test singletona Jeden")
		void testJedenSingleton() {
			Jeden jeden1 = Jeden.twórz();
			Jeden jeden2 = Jeden.twórz();
			assertSame(jeden1, jeden2, "Jeden powinno być singletonem");
			assertEquals(1.0, jeden1.policz(10.0), 0.001);
			assertEquals("1.0", jeden1.toString());
		}

		@Test
		@DisplayName("Test fabryki stałych")
		void testStałaFabryka() {
			Stała zero = Stała.twórz(0.0);
			Stała jeden = Stała.twórz(1.0);

			assertInstanceOf(Zero.class, zero);
			assertInstanceOf(Jeden.class, jeden);

			Stała pi = Stała.twórz(3.14159);
			assertEquals(3.14159, pi.policz(0), 0.00001);
			assertEquals("3.14159", pi.toString());
		}

		@Test
		@DisplayName("Test pochodnej stałej")
		void testPochodnaStałej() {
			Wyrażenie pochodna = dwa.pochodna();
			assertInstanceOf(Zero.class, pochodna);
			assertEquals(0.0, pochodna.policz(5.0), 0.001);
		}
	}

	// ========== TESTY ZMIENNEJ ==========

	@Nested
	@DisplayName("Testy klasy Zmienna")
	class ZmiennaTest {

		@Test
		@DisplayName("Test singletona Zmienna")
		void testZmiennaSingleton() {
			Zmienna x1 = Zmienna.twórz();
			Zmienna x2 = Zmienna.twórz();
			assertSame(x1, x2, "Zmienna powinna być singletonem");
		}

		@Test
		@DisplayName("Test obliczania wartości zmiennej")
		void testZmiennaPolicz() {
			assertEquals(5.0, x.policz(5.0), 0.001);
			assertEquals(-2.5, x.policz(-2.5), 0.001);
			assertEquals(0.0, x.policz(0.0), 0.001);
		}

		@Test
		@DisplayName("Test pochodnej zmiennej")
		void testPochodnaZmiennej() {
			Wyrażenie pochodna = x.pochodna();
			assertInstanceOf(Jeden.class, pochodna);
			assertEquals(1.0, pochodna.policz(100.0), 0.001);
		}

		@Test
		@DisplayName("Test toString zmiennej")
		void testZmiennaToString() {
			assertEquals("x", x.toString());
		}
	}

	// ========== TESTY OPERATORÓW ==========

	@Nested
	@DisplayName("Testy operatorów")
	class OperatoryTest {

		@Test
		@DisplayName("Test prostego dodawania")
		void testProsteDodawanie() {
			Wyrażenie suma = Plus.twórz(dwa, trzy);
			assertEquals(5.0, suma.policz(0.0), 0.001);
			assertEquals(5.0, suma.policz(10.0), 0.001); // Stałe nie zależą od x
		}

		@Test
		@DisplayName("Test dodawania z zmienną")
		void testDodowanieZeZmienną() {
			Wyrażenie suma = Plus.twórz(x, dwa);
			assertEquals(7.0, suma.policz(5.0), 0.001);
			assertEquals(12.0, suma.policz(10.0), 0.001);
		}

		@Test
		@DisplayName("Test mnożenia")
		void testMnożenie() {
			Razy iloczyn = new Razy(dwa, trzy);
			assertEquals(6.0, iloczyn.policz(0.0), 0.001);
			assertEquals(6.0, iloczyn.policz(5.0), 0.001);
		}

		@Test
		@DisplayName("Test mnożenia z zmienną")
		void testMnożenieZeZmienną() {
			Razy iloczyn = new Razy(x, dwa);
			assertEquals(10.0, iloczyn.policz(5.0), 0.001);
			assertEquals(0.0, iloczyn.policz(0.0), 0.001);
		}

		@Test
		@DisplayName("Test priorytetu operatorów")
		void testPriorytetOperatorów() {
			Plus plus = new Plus(x, dwa);
			Razy razy = new Razy(x, dwa);

			assertTrue(razy.priorytet() > plus.priorytet(),
					"Mnożenie powinno mieć wyższy priorytet niż dodawanie");
		}
	}

	// ========== TESTY UPRASZCZANIA ==========

	@Nested
	@DisplayName("Testy upraszczania wyrażeń")
	class UpraszczanieTest {

		@Test
		@DisplayName("Test upraszczania dodawania z zerem")
		void testDodawanieZeZerem() {
			Wyrażenie wynik1 = zero.dodaj(x);
			Wyrażenie wynik2 = x.dodaj(zero);

			assertSame(x, wynik1, "0 + x powinno dać x");
			assertSame(x, wynik2, "x + 0 powinno dać x");
		}

		@Test
		@DisplayName("Test upraszczania mnożenia przez zero")
		void testMnożeniePrzezZero() {
			Wyrażenie wynik1 = zero.pomnóż(x);
			Wyrażenie wynik2 = x.pomnóż(zero);

			assertInstanceOf(Zero.class, wynik1, "0 * x powinno dać 0");
			assertInstanceOf(Zero.class, wynik2, "x * 0 powinno dać 0");
		}

		@Test
		@DisplayName("Test upraszczania mnożenia przez jeden")
		void testMnożeniePrzezJeden() {
			Wyrażenie wynik1 = jeden.pomnóż(x);
			Wyrażenie wynik2 = x.pomnóż(jeden);

			assertSame(x, wynik1, "1 * x powinno dać x");
			assertSame(x, wynik2, "x * 1 powinno dać x");
		}

		@Test
		@DisplayName("Test upraszczania stałych")
		void testUpraszczanieStałych() {
			Wyrażenie suma = dwa.dodaj(trzy);
			assertEquals(5.0, suma.policz(0.0), 0.001);

			Wyrażenie iloczyn = dwa.pomnóż(trzy);
			assertEquals(6.0, iloczyn.policz(0.0), 0.001);
		}
	}

	// ========== TESTY FUNKCJI ==========

	@Nested
	@DisplayName("Testy funkcji matematycznych")
	class FunkcjeTest {

		@Test
		@DisplayName("Test funkcji sinus")
		void testSin() {
			Sin sinX = new Sin(x);
			assertEquals(Math.sin(0.5), sinX.policz(0.5), 0.001);
			assertEquals(Math.sin(Math.PI/2), sinX.policz(Math.PI/2), 0.001);
			assertEquals("sin(x)", sinX.toString());
		}

		@Test
		@DisplayName("Test funkcji cosinus")
		void testCos() {
			Cos cosX = new Cos(x);
			assertEquals(Math.cos(0.5), cosX.policz(0.5), 0.001);
			assertEquals(Math.cos(Math.PI), cosX.policz(Math.PI), 0.001);
			assertEquals("cos(x)", cosX.toString());
		}

		@Test
		@DisplayName("Test jednoargumentowego minusa")
		void testJMinus() {
			JMinus minusX = new JMinus(x);
			assertEquals(-5.0, minusX.policz(5.0), 0.001);
			assertEquals(3.0, minusX.policz(-3.0), 0.001);
			assertEquals("-(x)", minusX.toString());
		}

		@Test
		@DisplayName("Test funkcji złożonych")
		void testFunkcjeZłożone() {
			// sin(2*x)
			Razy dwaX = new Razy(dwa, x);
			Sin sin2X = new Sin(dwaX);

			assertEquals(Math.sin(2 * 1.5), sin2X.policz(1.5), 0.001);
			assertEquals("sin(2.0*x)", sin2X.toString());
		}
	}

	// ========== TESTY POCHODNYCH ==========

	@Nested
	@DisplayName("Testy obliczania pochodnych")
	class PochodneTesty {

		@Test
		@DisplayName("Test pochodnej sumy")
		void testPochodnaSumy() {
			// (x + 2)' = 1
			Plus suma = new Plus(x, dwa);
			Wyrażenie pochodna = suma.pochodna();
			assertEquals(1.0, pochodna.policz(5.0), 0.001);
		}

		@Test
		@DisplayName("Test pochodnej iloczynu")
		void testPochodnaIloczynu() {
			// (x * 3)' = 3
			Razy iloczyn = new Razy(x, trzy);
			Wyrażenie pochodna = iloczyn.pochodna();
			assertEquals(3.0, pochodna.policz(5.0), 0.001);
		}

		@Test
		@DisplayName("Test pochodnej sinusa")
		void testPochodnaSinusa() {
			// sin(x)' = cos(x)
			Sin sinX = new Sin(x);
			Wyrażenie pochodna = sinX.pochodna();

			double testValue = 1.0;
			assertEquals(Math.cos(testValue), pochodna.policz(testValue), 0.001);
		}

		@Test
		@DisplayName("Test pochodnej cosinusa")
		void testPochodnaCosinusa() {
			// cos(x)' = -sin(x)
			Cos cosX = new Cos(x);
			Wyrażenie pochodna = cosX.pochodna();

			double testValue = 1.0;
			assertEquals(-Math.sin(testValue), pochodna.policz(testValue), 0.001);
		}

		@Test
		@DisplayName("Test pochodnej funkcji złożonej")
		void testPochodnaFunkcjiZłożonej() {
			// sin(2*x)' = 2*cos(2*x)
			Razy dwaX = new Razy(dwa, x);
			Sin sin2X = new Sin(dwaX);
			Wyrażenie pochodna = sin2X.pochodna();

			double testValue = 0.5;
			double expected = 2 * Math.cos(2 * testValue);
			assertEquals(expected, pochodna.policz(testValue), 0.001);
		}
	}

	// ========== TESTY CAŁKOWANIA ==========

	@Nested
	@DisplayName("Testy całkowania numerycznego")
	class CałkowanieTest {

		@Test
		@DisplayName("Test całki z stałej")
		void testCałkaStałej() {
			// ∫[0,2] 3 dx = 6
			double wynik = trzy.całka(0.0, 2.0, 100);
			assertEquals(6.0, wynik, 0.01);
		}

		@Test
		@DisplayName("Test całki z funkcji liniowej")
		void testCałkaFunkcjiLiniowej() {
			// ∫[0,2] x dx = 2
			double wynik = x.całka(0.0, 2.0, 100);
			assertEquals(2.0, wynik, 0.01);
		}

		@Test
		@DisplayName("Test całki z funkcji kwadratowej")
		void testCałkaFunkcjiKwadratowej() {
			// ∫[0,1] x² dx = 1/3
			Razy xKwadrat = new Razy(x, x);
			double wynik = xKwadrat.całka(0.0, 1.0, 1000);
			assertEquals(1.0/3.0, wynik, 0.001);
		}

		@Test
		@DisplayName("Test całki z funkcji sinus")
		void testCałkaSinusa() {
			// ∫[0,π] sin(x) dx = 2
			Sin sinX = new Sin(x);
			double wynik = sinX.całka(0.0, Math.PI, 1000);
			assertEquals(2.0, wynik, 0.01);
		}
	}

	// ========== TESTY FORMATOWANIA ==========

	@Nested
	@DisplayName("Testy formatowania wyrażeń")
	class FormatowanieTest {

		@Test
		@DisplayName("Test nawiasów w wyrażeniach złożonych")
		void testNawiasy() {
			// (x + 2) * 3
			Plus suma = new Plus(x, dwa);
			Razy iloczyn = new Razy(suma, trzy);

			String reprezentacja = iloczyn.toString();
			assertTrue(reprezentacja.contains("(x+2.0)"),
					"Dodawanie w mnożeniu powinno być w nawiasach");
		}

		@Test
		@DisplayName("Test priorytetu bez nawiasów")
		void testPriorytetBezNawiasów() {
			// x * 2 + 3 (bez nawiasów wokół x*2)
			Razy iloczyn = new Razy(x, dwa);
			Plus suma = new Plus(iloczyn, trzy);

			String reprezentacja = suma.toString();
			assertFalse(reprezentacja.contains("(x*2.0)"),
					"Mnożenie w dodawaniu nie powinno być w nawiasach");
		}
	}

	// ========== TESTY PARAMETRYZOWANE ==========

	@ParameterizedTest
	@DisplayName("Test różnych wartości dla zmiennej")
	@ValueSource(doubles = {-10.0, -1.0, 0.0, 1.0, 5.0, 100.0})
	void testRóżneWartościZmiennej(double wartość) {
		assertEquals(wartość, x.policz(wartość), 0.001);
	}

	@ParameterizedTest
	@DisplayName("Test funkcji trygonometrycznych dla różnych wartości")
	@CsvSource({
			"0.0, 0.0, 1.0",
			"1.5708, 1.0, 0.0",  // π/2
			"3.1416, 0.0, -1.0", // π
			"4.7124, -1.0, 0.0"  // 3π/2
	})
	void testFunkcjeTrygonometryczne(double x, double expectedSin, double expectedCos) {
		Sin sinX = new Sin(this.x);
		Cos cosX = new Cos(this.x);

		assertEquals(expectedSin, sinX.policz(x), 0.001);
		assertEquals(expectedCos, cosX.policz(x), 0.001);
	}

	@Test
	@DisplayName("Test przykładu z Main")
	void testPrzykładZMain() {
		// Odtworzenie testów z klasy Main
		assertEquals("x", zero.dodaj(x).toString());
		assertEquals("x", x.dodaj(zero).toString());
		assertEquals("1.0", jeden.dodaj(zero).toString());
		assertEquals("0.0", zero.pomnóż(x).toString());
		assertEquals("0.0", x.pomnóż(zero).toString());
		assertEquals("x", jeden.pomnóż(x).toString());
		assertEquals("x", x.pomnóż(jeden).toString());
		assertEquals("0.0", zero.pomnóż(jeden).toString());
	}
}