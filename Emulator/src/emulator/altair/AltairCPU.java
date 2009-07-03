package emulator.altair;

import emulator.core.CPU;
import emulator.core.Control;
import emulator.core.Defs;
import emulator.core.Device;
import emulator.core.Register;
import emulator.core.Unit;

public class AltairCPU extends CPU {

	/* Memory */

	public static final int MAXMEMSIZE = 65536; /* max memory size */

	private int MEMSIZE = (MAXMEMSIZE); /* actual memory size */
	private long ADDRMASK = (MAXMEMSIZE - 1); /* address mask */

	public static final int UNIT_V_OPSTOP = (Unit.UNIT_V_UF); /*
															 * Stop on Invalid
															 * OP?
															 */
	public static final int UNIT_OPSTOP = (1 << UNIT_V_OPSTOP);
	public static final int UNIT_V_CHIP = (UNIT_V_UF + 1); /* 8080 or Z80 */
	public static final int UNIT_CHIP = (1 << UNIT_V_CHIP);
	public static final int UNIT_V_MSIZE = (UNIT_V_UF + 2); /* Memory Size */
	public static final int UNIT_MSIZE = (1 << UNIT_V_MSIZE);

	/* Simulator stop codes */

	public static final long STOP_RSRV = 1; /* must be 1 */
	public static final long STOP_HALT = 2; /* HALT */
	public static final long STOP_IBKPT = 3; /* breakpoint */
	public static final long STOP_OPCODE = 4;

	Register A, BC, DE, HL, C, Z, AC, S, P, PC, SR, INTE;

	Register SP;
	Register req, chip, PCX;
	
	long saved_PC;
	

	/**
	 * Constructor - set up the environment of the Altair CPU
	 */
	public AltairCPU() {
		
		// This is the CPU device.
		this.cpuDevice = this;


		memory.m = new byte[MEMSIZE]; // Memory
		
		this.

		// Accumulator
		A = new Register();
		A.name = "A";
		A.width = 8;
		A.value = 0;
		registers.put(A.name, A);

		/* BC register pair */
		BC = new Register();
		BC.name = "BC";
		BC.width = 16;
		BC.value = 0;
		registers.put(BC.name, BC);

		/* DE register pair */
		DE = new Register();
		DE.name = "DE";
		DE.width = 16;
		DE.value = 0;
		registers.put(DE.name, DE);

		/* HL register pair */
		HL = new Register();
		HL.name = "HL";
		HL.width = 16;
		HL.value = 0;
		registers.put(HL.name, HL);

		/* Stack pointer */
		SP = new Register();
		SP.name = "SP";
		SP.width = 16;
		SP.value = 0;
		registers.put(SP.name, SP);

		/* carry flag */
		C = new Register();
		C.name = "C";
		C.width = 1;
		C.value = 0;
		registers.put(C.name, C);

		/* Zero flag */
		Z = new Register();
		Z.name = "Z";
		Z.width = 1;
		Z.value = 0;
		registers.put(Z.name, Z);

		/* Aux carry */
		AC = new Register();
		AC.name = "AC";
		AC.width = 1;
		AC.value = 0;
		registers.put(AC.name, AC);

		/* sign flag */
		S = new Register();
		S.name = "S";
		S.width = 1;
		S.value = 0;
		registers.put(S.name, S);

		/* parity flag */
		P = new Register();
		P.name = "P";
		P.width = 1;
		P.value = 0;
		registers.put(P.name, P);

		/* program counter */
		PC = new Register();
		PC.name = "PC";
		PC.width = 16;
		PC.value = 0;
		registers.put(PC.name, PC);

		/* switch register */
		SR = new Register();
		SR.name = "SR";
		SR.width = 16;
		SR.value = 0;
		registers.put(SR.name, SR);

		/* Interrupt Enable */
		INTE = new Register();
		INTE.name = "INTE";
		INTE.width = 1;
		INTE.value = 0;
		registers.put(INTE.name, INTE);

		/* Interrupt request */
		req = new Register();
		req.name = "REQ";
		req.width = 1;
		req.value = 0;
		registers.put(req.name, req);

		/* 0 = 8080 chip, 1 = z80 chip */
		chip = new Register();
		chip.name = "CHIP";
		chip.width = 1;
		chip.value = 0;
		registers.put(chip.name, chip);

		/* External view of PC */
		PCX = new Register();
		PCX.name = "PCX";
		PCX.width = 16;
		PCX.value = 0;
		registers.put(PCX.name, PCX);

	}

	@Override
	public void deposit(long val, long addr, Unit uptr, long sw) {
	    if (addr >= MEMSIZE) return;// SCPE_NXM;
	    memory.m[(int) addr] = (byte) (val & 0377);
	    return; // SCPE_OK;

	}

	@Override
	public long examine(long addr, Unit uptr, long sw) {
		if (addr >= MEMSIZE) return 0; //SCPE_NXM;
		return memory.m[(int) addr] & 0377;

	}

	@Override
	public void reset() {
		C.value = 0;
		Z.value = 0;
		saved_PC = 0;
		INTE.value = 0;
		sim_brk_types = sim_brk_dflt = Defs.SWMASK ('E');
		return;

	}

	@Override
	public long run() {

		int IR, OP, reason, hi, lo, carry, i;
		long DAR;

		int PC;

		PC = (int) (saved_PC & ADDRMASK); /* load local PC */
		C.value = C.value & 0200000;
		reason = 0;

		/* Main instruction fetch/decode loop */

		while (reason == 0) { /* loop until halted */
			if (Control.sim_interval <= 0) { /* check clock queue */
				if (reason = sim_process_event())
					break;
			}

			if (INTE.value > 0) { /* interrupt? */

				/*
				 * 8080 interrupts not implemented yet. None were used, on a
				 * standard Altair 8800. All I/O is programmed.
				 */

			} /* end interrupt */

			if (sim_brk_summ && sim_brk_test(PC, Defs.SWMASK('E'))) { /* breakpoint? */
				reason = (int) STOP_IBKPT; /* stop simulation */
				break;
			}

			if (PC == 0177400) { /* BOOT PROM address */
				for (i = 0; i < 250; i++) {
					memory.m[(int) (i + 0177400)] = bootrom[i] & 0xFF;
				}
			}

			PCX.value = PC;

			IR = OP = memory.m[PC]; /* fetch instruction */

			PC = (int) ((PC + 1) & ADDRMASK); /* increment PC */

			Control.sim_interval--;

			if (OP == 0166) { /* HLT Instruction */
				reason = (int) STOP_HALT;
				PC--;
				continue;
			}

			/*
			 * Handle below all operations which refer to registers or register
			 * pairs. After that, a large switch statement takes care of all
			 * other opcodes
			 */

			if ((OP & 0xC0) == 0x40) { /* MOV */
				DAR = getreg(OP & 0x07);
				putreg((OP >> 3) & 0x07, DAR);
				continue;
			}
			if ((OP & 0xC7) == 0x06) { /* MVI */
				putreg((OP >> 3) & 0x07, memory.m[PC]);
				PC++;
				continue;
			}
			if ((OP & 0xCF) == 0x01) { /* LXI */
				DAR = memory.m[PC] & 0x00ff;
				PC++;
				DAR = DAR | (memory.m[PC] << 8) & 0xFF00;
				;
				putpair((OP >> 4) & 0x03, DAR);
				PC++;
				continue;
			}
			if ((OP & 0xEF) == 0x0A) { /* LDAX */
				DAR = getpair((OP >> 4) & 0x03);
				putreg(7, memory.m[(int) DAR]);
				continue;
			}
			if ((OP & 0xEF) == 0x02) { /* STAX */
				DAR = getpair((OP >> 4) & 0x03);
				memory.m[(int) DAR] = (byte) getreg(7);
				continue;
			}

			if ((OP & 0xF8) == 0xB8) { /* CMP */
				DAR = A.value & 0xFF;
				DAR -= getreg(OP & 0x07);
				setarith(DAR);
				continue;
			}
			if ((OP & 0xC7) == 0xC2) { /* JMP <condition> */
				if (cond((OP >> 3) & 0x07) == 1) {
					lo = memory.m[PC];
					PC++;
					hi = memory.m[PC];
					PC++;
					PC = (hi << 8) + lo;
				} else {
					PC += 2;
				}
				continue;
			}
			if ((OP & 0xC7) == 0xC4) { /* CALL <condition> */
				if (cond((OP >> 3) & 0x07) == 1) {
					lo = memory.m[PC];
					PC++;
					hi = memory.m[PC];
					PC++;
					SP.value--;
					memory.m[(int) SP.value] = (byte) ((PC >> 8) & 0xff);
					SP.value--;
					memory.m[(int) SP.value] = (byte) (PC & 0xff);
					PC = (hi << 8) + lo;
				} else {
					PC += 2;
				}
				continue;
			}
			if ((OP & 0xC7) == 0xC0) { /* RET <condition> */
				if (cond((OP >> 3) & 0x07) == 1) {
					PC = memory.m[(int) SP.value];
					SP.value++;
					PC |= (memory.m[(int) SP.value] << 8) & 0xff00;
					SP.value++;
				}
				continue;
			}
			if ((OP & 0xC7) == 0xC7) { /* RST */
				SP.value--;
				memory.m[(int) SP.value] = (byte) ((PC >> 8) & 0xff);
				SP.value--;
				memory.m[(int) SP.value] = (byte) (PC & 0xff);
				PC = OP & 0x38;
				continue;
			}

			if ((OP & 0xCF) == 0xC5) { /* PUSH */
				DAR = getpush((OP >> 4) & 0x03);
				SP.value--;
				memory.m[(int) SP.value] = (byte) ((DAR >> 8) & 0xff);
				SP.value--;
				memory.m[(int) SP.value] = (byte) (DAR & 0xff);
				continue;
			}
			if ((OP & 0xCF) == 0xC1) { /* POP */
				DAR = memory.m[(int) SP.value];
				SP.value++;
				DAR |= memory.m[(int) SP.value] << 8;
				SP.value++;
				putpush((OP >> 4) & 0x03, DAR);
				continue;
			}
			if ((OP & 0xF8) == 0x80) { /* ADD */
				A.value += getreg(OP & 0x07);
				setarith(A.value);
				A.value = A.value & 0xFF;
				continue;
			}
			if ((OP & 0xF8) == 0x88) { /* ADC */
				carry = 0;
				if (C.value != 0)
					carry = 1;
				A.value += getreg(OP & 0x07);
				A.value += carry;
				setarith(A.value);
				A.value = A.value & 0xFF;
				continue;
			}
			if ((OP & 0xF8) == 0x90) { /* SUB */
				A.value -= getreg(OP & 0x07);
				setarith(A.value);
				A.value = A.value & 0xFF;
				continue;
			}
			if ((OP & 0xF8) == 0x98) { /* SBB */
				carry = 0;
				if (C.value != 0)
					carry = 1;
				A.value -= (getreg(OP & 0x07)) + carry;
				setarith(A.value);
				A.value = A.value & 0xFF;
				continue;
			}
			if ((OP & 0xC7) == 0x04) { /* INR */
				DAR = getreg((OP >> 3) & 0x07);
				DAR++;
				setinc(DAR);
				DAR = DAR & 0xFF;
				putreg((OP >> 3) & 0x07, DAR);
				continue;
			}
			if ((OP & 0xC7) == 0x05) { /* DCR */
				DAR = getreg((OP >> 3) & 0x07);
				DAR--;
				setinc(DAR);
				DAR = DAR & 0xFF;
				putreg((OP >> 3) & 0x07, DAR);
				continue;
			}
			if ((OP & 0xCF) == 0x03) { /* INX */
				DAR = getpair((OP >> 4) & 0x03);
				DAR++;
				DAR = DAR & 0xFFFF;
				putpair((OP >> 4) & 0x03, DAR);
				continue;
			}
			if ((OP & 0xCF) == 0x0B) { /* DCX */
				DAR = getpair((OP >> 4) & 0x03);
				DAR--;
				DAR = DAR & 0xFFFF;
				putpair((OP >> 4) & 0x03, DAR);
				continue;
			}
			if ((OP & 0xCF) == 0x09) { /* DAD */
				HL.value += getpair((OP >> 4) & 0x03);
				C.value = 0;
				if ((HL.value & 0x10000) != 0)
					C.value = 0200000;
				HL.value = HL.value & 0xFFFF;
				continue;
			}
			if ((OP & 0xF8) == 0xA0) { /* ANA */
				A.value &= getreg(OP & 0x07);
				C.value = 0;
				setlogical(A.value);
				A.value &= 0xFF;
				continue;
			}
			if ((OP & 0xF8) == 0xA8) { /* XRA */
				A.value ^= getreg(OP & 0x07);
				C.value = 0;
				setlogical(A.value);
				A.value &= 0xFF;
				continue;
			}
			if ((OP & 0xF8) == 0xB0) { /* ORA */
				A.value |= getreg(OP & 0x07);
				C.value = 0;
				setlogical(A.value);
				A.value &= 0xFF;
				continue;
			}

			/* The Big Instruction Decode Switch */

			switch (IR) {

			/* Logical instructions */

			case 0376: { /* CPI */
				DAR = A.value & 0xFF;
				DAR -= memory.m[PC];
				PC++;
				setarith(DAR);
				break;
			}
			case 0346: { /* ANI */
				A.value &= memory.m[PC];
				PC++;
				C.value = 0;
				AC.value = 0;
				setlogical(A.value);
				A.value &= 0xFF;
				break;
			}
			case 0356: { /* XRI */
				A.value ^= memory.m[PC];
				PC++;
				C.value = 0;
				AC.value = 0;
				setlogical(A.value);
				A.value &= 0xFF;
				break;
			}
			case 0366: { /* ORI */
				A.value |= memory.m[PC];
				PC++;
				C.value = 0;
				AC.value = 0;
				setlogical(A.value);
				A.value &= 0xFF;
				break;
			}

				/* Jump instructions */

			case 0303: { /* JMP */
				lo = memory.m[PC];
				PC++;
				hi = memory.m[PC];
				PC++;
				PC = (hi << 8) + lo;
				break;
			}
			case 0351: { /* PCHL */
				PC = (int) HL.value;
				break;
			}
			case 0315: { /* CALL */
				lo = memory.m[PC];
				PC++;
				hi = memory.m[PC];
				PC++;
				SP.value--;
				memory.m[(int) SP.value] = (byte) ((PC >> 8) & 0xff);
				SP.value--;
				memory.m[(int) SP.value] = (byte) (PC & 0xff);
				PC = (hi << 8) + lo;
				break;
			}
			case 0311: { /* RET */
				PC = memory.m[(int) SP.value];
				SP.value++;
				PC |= (memory.m[(int) SP.value] << 8) & 0xff00;
				SP.value++;
				break;
			}

				/* Data Transfer Group */

			case 062: { /* STA */
				lo = memory.m[PC];
				PC++;
				hi = memory.m[PC];
				PC++;
				DAR = (hi << 8) + lo;
				memory.m[(int) DAR] = (byte) A.value;
				break;
			}
			case 072: { /* LDA */
				lo = memory.m[PC];
				PC++;
				hi = memory.m[PC];
				PC++;
				DAR = (hi << 8) + lo;
				A.value = memory.m[(int) DAR];
				break;
			}
			case 042: { /* SHLD */
				lo = memory.m[PC];
				PC++;
				hi = memory.m[PC];
				PC++;
				DAR = (hi << 8) + lo;
				memory.m[(int) DAR] = (byte) HL.value;
				DAR++;
				memory.m[(int) DAR] = (byte) ((HL.value >> 8) & 0x00ff);
				break;
			}
			case 052: { /* LHLD */
				lo = memory.m[PC];
				PC++;
				hi = memory.m[PC];
				PC++;
				DAR = (hi << 8) + lo;
				HL.value = memory.m[(int) DAR];
				DAR++;
				HL.value = HL.value | (memory.m[(int) DAR] << 8);
				break;
			}
			case 0353: { /* XCHG */
				DAR = HL.value;
				HL.value = DE.value;
				DE.value = DAR;
				break;
			}

				/* Arithmetic Group */

			case 0306: { /* ADI */
				A.value += memory.m[PC];
				PC++;
				setarith(A.value);
				A.value = A.value & 0xFF;
				break;
			}
			case 0316: { /* ACI */
				carry = 0;
				if (C.value != 0)
					carry = 1;
				A.value += memory.m[PC];
				A.value += carry;
				PC++;
				setarith(A.value);
				A.value = A.value & 0xFF;
				break;
			}
			case 0326: { /* SUI */
				A.value -= memory.m[PC];
				PC++;
				setarith(A.value);
				A.value = A.value & 0xFF;
				break;
			}
			case 0336: { /* SBI */
				carry = 0;
				if (C.value != 0)
					carry = 1;
				A.value -= (memory.m[PC] + carry);
				PC++;
				setarith(A.value);
				A.value = A.value & 0xFF;
				break;
			}
			case 047: { /* DAA */
				DAR = A.value & 0x0F;
				if (DAR > 9 || AC.value > 0) {
					DAR += 6;
					A.value &= 0xF0;
					A.value |= DAR & 0x0F;
					if ((DAR & 0x10) !=0)
						AC.value = 0200000;
					else
						AC.value = 0;
				}
				DAR = (A.value >> 4) & 0x0F;
				if (DAR > 9 || AC.value > 0) {
					DAR += 6;
					if (AC.value != 0)
						DAR++;
					A.value &= 0x0F;
					A.value |= (DAR << 4);
				}
				if (((DAR << 4) & 0x100) !=0)
					C.value = 0200000;
				else
					C.value = 0;
				if ((A.value & 0x80) != 0) {
					S.value = 0200000;
				} else {
					S.value = 0;
				}
				if ((A.value & 0xff) == 0)
					Z.value = 0200000;
				else
					Z.value = 0;
				parity(A.value);
				A.value = A.value & 0xFF;
				break;
			}
			case 07: { /* RLC */
				C.value = 0;
				C.value = (A.value << 9) & 0200000;
				A.value = (A.value << 1) & 0xFF;
				if (C.value != 0)
					A.value |= 0x01;
				break;
			}
			case 017: { /* RRC */
				C.value = 0;
				if ((A.value & 0x01) == 1)
					C.value |= 0200000;
				A.value = (A.value >> 1) & 0xFF;
				if (C.value != 0)
					A.value |= 0x80;
				break;
			}
			case 027: { /* RAL */
				DAR = C.value;
				C.value = 0;
				C.value = (A.value << 9) & 0200000;
				A.value = (A.value << 1) & 0xFF;
				if (DAR != 0)
					A.value |= 1;
				else
					A.value &= 0xFE;
				break;
			}
			case 037: { /* RAR */
				DAR = C.value;
				C.value = 0;
				if ((A.value & 0x01) == 1)
					C.value |= 0200000;
				A.value = (A.value >> 1) & 0xFF;
				if (DAR != 0)
					A.value |= 0x80;
				else
					A.value &= 0x7F;
				break;
			}
			case 057: { /* CMA */
				A.value = ~A.value;
				A.value &= 0xFF;
				break;
			}
			case 077: { /* CMC */
				C.value = ~C.value;
				C.value &= 0200000;
				break;
			}
			case 067: { /* STC */
				C.value = 0200000;
				break;
			}

				/* Stack, I/O & Machine Control Group */

			case 0: { /* NOP */
				break;
			}
			case 0343: { /* XTHL */
				lo = memory.m[(int) SP.value];
				hi = memory.m[(int) (SP.value + 1)];
				memory.m[(int) SP.value] = (byte) (HL.value & 0xFF);
				memory.m[(int) (SP.value + 1)] = (byte) ((HL.value >> 8) & 0xFF);
				HL.value = (hi << 8) + lo;
				break;
			}
			case 0371: { /* SPHL */
				SP.value = HL.value;
				break;
			}
			case 0373: { /* EI */
				INTE.value = 0200000;
				break;
			}
			case 0363: { /* DI */
				INTE.value = 0;
				break;
			}
			case 0333: { /* IN */
				DAR = memory.m[PC] & 0xFF;
				PC++;
				if (DAR == 0xFF) {
					A.value = (SR.value >> 8) & 0xFF;
				} else {
					A.value = dev_table[DAR].routine(0, 0);
				}
				break;
			}
			case 0323: { /* OUT */
				DAR = memory.m[PC] & 0xFF;
				PC++;
				dev_table[DAR].routine(1, A.value);
				break;
			}

			default: {
				if ((flags & UNIT_OPSTOP) != 0) {
					reason = (int) STOP_OPCODE;
					PC--;
				}
				break;
			}
			}
		}

		/* Simulation halted */

		saved_PC = PC;
		return reason;
	}

	@Override
	public void setSize(Unit uptr, long val, String cptr) {
		// TODO Auto-generated method stub

	}

	@Override
	public int action(Unit up) {
		// TODO Auto-generated method stub
		return 0;
	}

	// Utility routines, macros, etc
	private boolean MEM_ADDR_OK(long x) {
		return ((((x)) < MEMSIZE));
	}

	/* Test an 8080 flag condition and return 1 if true, 0 if false */
	int cond(int con) {
		switch (con) {
		case 0:
			if (Z.value == 0)
				return (1);
			break;
		case 1:
			if (Z.value != 0)
				return (1);
			break;
		case 2:
			if (C.value == 0)
				return (1);
			break;
		case 3:
			if (C.value != 0)
				return (1);
			break;
		case 4:
			if (P.value == 0)
				return (1);
			break;
		case 5:
			if (P.value != 0)
				return (1);
			break;
		case 6:
			if (S.value == 0)
				return (1);
			break;
		case 7:
			if (S.value != 0)
				return (1);
			break;
		default:
			break;
		}
		return (0);
	}

	/*
	 * Set the <C>arry, <S>ign, <Z>ero and <P>arity flags following an
	 * arithmetic operation on 'reg'.
	 */

	void setarith(long dAR) {
		int bc = 0;

		if ((dAR & 0x100) != 0)
			C.value = 0200000;
		else
			C.value = 0;
		if ((dAR & 0x80) != 0) {
			bc++;
			S.value = 0200000;
		} else {
			S.value = 0;
		}
		if ((dAR & 0xff) == 0)
			Z.value = 0200000;
		else
			Z.value = 0;
		AC.value = 0;
		if ((flags & UNIT_CHIP) != 0) {
			P.value = 0; /* parity is zero for *all* arith ops on Z80 */
		} else {
			parity(dAR);
		}
	}

	/*
	 * Set the <C>arry, <S>ign, <Z>ero amd <P>arity flags following a logical
	 * (bitwise) operation on 'reg'.
	 */

	void setlogical(long value) {
		C.value = 0;
		if ((value & 0x80) != 0) {
			S.value = 0200000;
		} else {
			S.value = 0;
		}
		if ((value & 0xff) == 0)
			Z.value = 0200000;
		else
			Z.value = 0;
		AC.value = 0;
		parity(value);
	}

	/*
	 * Set the Parity (P) flag based on parity of 'reg', i.e., number of bits on
	 * even: P=0200000, else P=0
	 */

	void parity(long dAR) {
		int bc = 0;

		if ((dAR & 0x01) != 0)
			bc++;
		if ((dAR & 0x02) != 0)
			bc++;
		if ((dAR & 0x04) != 0)
			bc++;
		if ((dAR & 0x08) != 0)
			bc++;
		if ((dAR & 0x10) != 0)
			bc++;
		if ((dAR & 0x20) != 0)
			bc++;
		if ((dAR & 0x40) != 0)
			bc++;
		if ((dAR & 0x80) != 0)
			bc++;
		P.value = ~(bc << 16);
		P.value &= 0200000;
	}

	/*
	 * Set the <S>ign, <Z>ero amd <P>arity flags following an INR/DCR operation
	 * on 'reg'.
	 */

	void setinc(long dAR) {
		int bc = 0;

		if ((dAR & 0x80) != 0) {
			bc++;
			S.value = 0200000;
		} else {
			S.value = 0;
		}
		if ((dAR & 0xff) == 0)
			Z.value = 0200000;
		else
			Z.value = 0;
		if ((flags & UNIT_CHIP) != 0) {
			P.value = 0; /* parity is zero for *all* arith ops on Z80 */
		} else {
			parity(dAR);
		}
	}

	/* Get an 8080 register and return it */
	long getreg(int reg) {
		switch (reg) {
		case 0:
			return ((BC.value >> 8) & 0x00ff);
		case 1:
			return (BC.value & 0x00FF);
		case 2:
			return ((DE.value >> 8) & 0x00ff);
		case 3:
			return (DE.value & 0x00ff);
		case 4:
			return ((HL.value >> 8) & 0x00ff);
		case 5:
			return (HL.value & 0x00ff);
		case 6:
			return (memory.m[(int) HL.value]);
		case 7:
			return (A.value);
		default:
			break;
		}
		return 0;
	}

	/* Put a value into an 8080 register from memory */
	void putreg(int reg, long dAR) {
		switch (reg) {
		case 0:
			BC.value = BC.value & 0x00FF;
			BC.value = BC.value | (dAR << 8);
			break;
		case 1:
			BC.value = BC.value & 0xFF00;
			BC.value = BC.value | dAR;
			break;
		case 2:
			DE.value = DE.value & 0x00FF;
			DE.value = DE.value | (dAR << 8);
			break;
		case 3:
			DE.value = DE.value & 0xFF00;
			DE.value = DE.value | dAR;
			break;
		case 4:
			HL.value = HL.value & 0x00FF;
			HL.value = HL.value | (dAR << 8);
			break;
		case 5:
			HL.value = HL.value & 0xFF00;
			HL.value = HL.value | dAR;
			break;
		case 6:
			memory.m[(int) HL.value] = (byte) (dAR & 0xff);
			break;
		case 7:
			A.value = dAR & 0xff;
		default:
			break;
		}
	}

	/* Return the value of a selected register pair */
	long getpair(int reg) {
		switch (reg) {
		case 0:
			return (BC.value);
		case 1:
			return (DE.value);
		case 2:
			return (HL.value);
		case 3:
			return (SP.value);
		default:
			break;
		}
		return 0;
	}

	/*
	 * Return the value of a selected register pair, in PUSH format where 3
	 * means A& flags, not SP
	 */
	long getpush(int reg) {
		int stat;

		switch (reg) {
		case 0:
			return (BC.value);
		case 1:
			return (DE.value);
		case 2:
			return (HL.value);
		case 3:
			stat = (int) (A.value << 8);
			if (S.value != 0)
				stat |= 0x80;
			if (Z.value != 0)
				stat |= 0x40;
			if (AC.value != 0)
				stat |= 0x10;
			if (P.value != 0)
				stat |= 0x04;
			stat |= 0x02;
			if (C.value != 0)
				stat |= 0x01;
			return (stat);
		default:
			break;
		}
		return 0;
	}

	/*
	 * Place data into the indicated register pair, in PUSH format where 3 means
	 * A& flags, not SP
	 */
	void putpush(int reg, long dAR) {
		switch (reg) {
		case 0:
			BC.value = dAR;
			break;
		case 1:
			DE.value = dAR;
			break;
		case 2:
			HL.value = dAR;
			break;
		case 3:
			A.value = (dAR >> 8) & 0xff;
			S.value = 0;
			Z.value = 0;
			AC.value = 0;
			P.value = 0;
			C.value = 0;
			if ((dAR & 0x80) != 0)
				S.value = 0200000;
			if ((dAR & 0x40) != 0)
				Z.value = 0200000;
			if ((dAR & 0x10) != 0)
				AC.value = 0200000;
			if ((dAR & 0x04) != 0)
				P.value = 0200000;
			if ((dAR & 0x01) != 0)
				C.value = 0200000;
			break;
		default:
			break;
		}
	}

	/* Put a value into an 8080 register pair */
	void putpair(int reg, long dAR) {
		switch (reg) {
		case 0:
			BC.value = dAR;
			break;
		case 1:
			DE.value = dAR;
			break;
		case 2:
			HL.value = dAR;
			break;
		case 3:
			SP.value = dAR;
			break;
		default:
			break;
		}
	}

	@Override
	public int attach(Unit up, String cp) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int boot(int u, Device dp) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int deposit(long v, long a, Unit up, int sw) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int detach(Unit up) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int examine(long v, long a, Unit up, int sw) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int msize(Unit up, int v, String cp, Device dp) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int reset(Device dp) {
		// TODO Auto-generated method stub
		return 0;
	}

}
