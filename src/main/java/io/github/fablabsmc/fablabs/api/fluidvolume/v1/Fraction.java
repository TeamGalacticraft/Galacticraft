package io.github.fablabsmc.fablabs.api.fluidvolume.v1;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import java.util.Objects;
import java.util.stream.IntStream;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.math.IntMath.gcd;

/**
 * DISCLAIMER: ALL CODE HERE NOT FINAL, MAY ENCOUNTER BREAKING CHANGES REGULARLY
 *
 * This is a patched version of the fluid API as it seems that deserialization and serialization doesn't work properly
 */
public final class Fraction extends Number implements Comparable<Fraction> {
	public static final Fraction ZERO = new Fraction(0, 1);
	public static final Fraction ONE = new Fraction(1, 1);
	public static final Codec<Fraction> CODEC = new Serializer();

	private final int numerator;
	private final /*Positive*/ int denominator;

	// should be only called if denom is positive and num & denom are coprime
	private Fraction(int numerator, int denominator) {
		assert denominator > 0 : "invalid denominator (must be positive)";
		assert gcd(Math.abs(numerator), denominator) == 1 : "not simplified";
		this.numerator = numerator;
		this.denominator = denominator;
	}

	public static Fraction of(int numerator, int denominator) {
		if (denominator == 0) throw new ArithmeticException("Zero denominator");
		return denominator < 0 ? ofValidDenominator(-numerator, -denominator) : ofValidDenominator(numerator, denominator);
	}

	// should be only called if denom is positive
	@VisibleForTesting
	static Fraction ofValidDenominator(int numerator, int denominator) {
		if (numerator == 0) return ZERO;
		if (numerator == denominator) return ONE;

		int gcd = gcd(Math.abs(numerator), denominator);

		return new Fraction(numerator / gcd, denominator / gcd);
	}

	//TODO: What shortcuts should we have, and how should we name them? This is in a Minecraft context, after all.
	public static Fraction ofWhole(int numerator) {
		if (numerator == 0) return ZERO;
		if (numerator == 1) return ONE;
		return new Fraction(numerator, 1);
	}

	public static Fraction ofThirds(int numerator) {
		return ofValidDenominator(numerator, 3);
	}

	public static Fraction ofNinths(int numerator) {
		return ofValidDenominator(numerator, 9);
	}

	public static Fraction ofThousandths(int numerator) {
		return ofValidDenominator(numerator, 1000);
	}

	public int getNumerator() {
		return numerator;
	}

	public int getDenominator() {
		return denominator;
	}

	public boolean isNegative() {
		return numerator < 0;
	}

	public boolean isPositive() {
		return numerator > 0;
	}

	public int signum() {
		return Integer.signum(numerator);
	}

	@Override
	public double doubleValue() {
		return ((double) numerator) / denominator;
	}

	public Fraction negate() {
		// don't need to simplify
		return new Fraction(-numerator, denominator);
	}

	public Fraction inverse() throws ArithmeticException {
		// don't need to simplify
		switch (signum()) {
			case 1:
				return new Fraction(denominator, numerator);
			case -1:
				return new Fraction(-denominator, -numerator);
			default:
				throw new ArithmeticException("Cannot invert zero fraction!");
		}
	}

	public Fraction add(Fraction other) {
		int commonMultiple = lcm(this.denominator, other.denominator);
		int leftNumerator = commonMultiple / this.denominator * this.numerator;
		int rightNumerator = commonMultiple / other.denominator * other.numerator;
		return ofValidDenominator(leftNumerator + rightNumerator, commonMultiple);
	}

	public Fraction subtract(Fraction other) {
		int commonMultiple = lcm(this.denominator, other.denominator);
		int leftNumerator = commonMultiple / this.denominator * this.numerator;
		int rightNumerator = commonMultiple / other.denominator * other.numerator;
		return ofValidDenominator(leftNumerator - rightNumerator, commonMultiple);
	}

	public Fraction multiply(Fraction other) {
		int gcd1 = gcd(Math.abs(this.numerator), other.denominator);
		int gcd2 = gcd(this.denominator, Math.abs(other.numerator));
		// guaranteed simplified
		return new Fraction(signum() * other.signum() * (this.numerator / gcd1) * (other.numerator / gcd2), (this.denominator / gcd2) * (other.denominator / gcd1));
	}

	public Fraction divide(Fraction other) {
		return multiply(other.inverse());
	}

	public Fraction floorWithDenominator(int denom) {
		checkArgument(denom > 0, "New denominator must be positive!");
		return Fraction.ofValidDenominator(Math.floorDiv(this.numerator * denom, this.denominator), denom);
	}

	public static Fraction add(Fraction... addends) {
		final int len;
		if ((len = addends.length) == 0) return ZERO;
		Fraction first = addends[0];

		int denominator = first.denominator;

		for (int i = 1; i < len; i++) {
			denominator = lcm(denominator, addends[i].denominator);
		}

		int numerator = 0;

		for (Fraction addend : addends) {
			numerator += denominator / addend.denominator * addend.numerator;
		}

		return ofValidDenominator(numerator, denominator);
	}

	public static Fraction subtract(Fraction minuend, Fraction subtrahend) {
		return minuend.subtract(subtrahend);
	}

	public static Fraction multiply(Fraction... factors) {
		final int len;
		if ((len = factors.length) == 0) return ONE;

		Fraction first = factors[0];
		int signum;
		if ((signum = first.signum()) == 0) return ZERO; // shortcut
		int numerator = Math.abs(first.numerator);
		int denominator = first.denominator;

		for (int i = 1; i < len; i++) {
			Fraction factor = factors[i];
			signum *= factor.signum();
			if (signum == 0) return ZERO; // shortcut
			int factorDenom = factor.denominator;
			int factorNum = Math.abs(factor.numerator);
			int gcd1 = gcd(numerator, factorDenom);
			int gcd2 = gcd(denominator, factorNum);
			numerator = (numerator / gcd1) * (factorNum / gcd2);
			denominator = (denominator / gcd2) * (factorDenom / gcd1);
			assert gcd(numerator, denominator) == 1;
		}

		// should be simplified by here
		return new Fraction(numerator * signum, denominator);
	}

	public static Fraction divide(Fraction dividend, Fraction divisor) {
		return dividend.divide(divisor);
	}

	private static int lcm(int a, int b) {
		return a / gcd(a, b) * b; // divide first to prevent overflow
	}

	@Override
	public int compareTo(Fraction other) {
		int leftNum = this.numerator * other.denominator;
		int rightNum = other.numerator * this.denominator;
		// todo integer overflow
		return Integer.compare(leftNum, rightNum);
	}

	public static Fraction max(Fraction left, Fraction right) {
		return left.compareTo(right) > 0 ? left : right;
	}

	public static Fraction min(Fraction left, Fraction right) {
		return left.compareTo(right) < 0 ? left : right;
	}

	@Override
	public int intValue() {
		return numerator / denominator;
	}

	@Override
	public long longValue() {
		return intValue();
	}

	@Override
	public float floatValue() {
		return (float) doubleValue();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Fraction fraction = (Fraction) o;
		return numerator == fraction.numerator && denominator == fraction.denominator;
	}

	@Override
	public int hashCode() {
		return Objects.hash(numerator, denominator);
	}

	@Override
	public String toString() {
		return numerator + "/" + denominator;
	}

	private static final class Serializer implements Codec<Fraction> {

		private Serializer() {

		}

		@Override
		public <T> DataResult<Pair<Fraction, T>> decode(DynamicOps<T> ops, T input) {
			return ops.getIntStream(input).map(stream -> {
				int[] arr = stream.toArray();
				switch (arr.length) {
					case 0:
						return ZERO;
					case 1:
						return ofWhole(arr[0]);
					default:
						return of(arr[0], arr[1]);
				}
			}).map(it -> Pair.of(it, ops.empty()));
		}

		@Override
		public <T> DataResult<T> encode(Fraction input, DynamicOps<T> ops, T prefix) {
			return DataResult.success(ops.createIntList(IntStream.of(input.numerator, input.denominator)));
		}
	}
}