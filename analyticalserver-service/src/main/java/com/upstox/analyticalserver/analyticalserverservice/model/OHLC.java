package com.upstox.analyticalserver.analyticalserverservice.model;

import java.io.Serializable;

public class OHLC implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6419082987145220688L;
	private double o = 0.0;
	private double c = 0.0;
	private double h = 0.0;
	private double l = 0.0;

	public OHLC(double open, double high, double low, double close) {
		this.o = open;
		this.c = close;
		this.h = high;
		this.l = low;
	}

	public double getO() {
		return o;
	}

	public void setO(double o) {
		this.o = o;
	}

	public double getC() {
		return c;
	}

	public void setC(double c) {
		this.c = c;
	}

	public double getH() {
		return h;
	}

	public void setH(double h) {
		this.h = h;
	}

	public double getL() {
		return l;
	}

	public void setL(double l) {
		this.l = l;
	}

	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (!(obj instanceof OHLC)) {
			return false;
		} else {
			OHLC that = (OHLC) obj;
			return this.o != that.o ? false : (this.c != that.c ? false : (this.h != that.h ? false : this.l == that.l));
		}
	}

	@Override
	public String toString() {
		return "OHLC [o=" + o + ", c=" + c + ", h=" + h + ", l=" + l + "]";
	}

}
