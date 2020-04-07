package com.gregei.gboy.core;

/**
 * This class emulates the Gameboy lcd.
 */
public class Z80 {

  public final boolean show = false;
  private final int[] conditionalTakenOpcodeTable = new int[]{
      1, 3, 2, 2, 1, 1, 2, 1, 5, 2, 2, 2, 1, 1, 2, 1,
      0, 3, 2, 2, 1, 1, 2, 1, 3, 2, 2, 2, 1, 1, 2, 1,
      3, 3, 2, 2, 1, 1, 2, 1, 3, 2, 2, 2, 1, 1, 2, 1,
      3, 3, 2, 2, 3, 3, 3, 1, 3, 2, 2, 2, 1, 1, 2, 1,
      1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 1,
      1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 1,
      1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 1,
      2, 2, 2, 2, 2, 2, 0, 2, 1, 1, 1, 1, 1, 1, 2, 1,
      1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 1,
      1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 1,
      1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 1,
      1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 1,
      5, 3, 4, 4, 6, 4, 2, 4, 5, 4, 4, 0, 6, 6, 2, 4,
      5, 3, 4, 0, 6, 4, 2, 4, 5, 4, 4, 0, 6, 0, 2, 4,
      3, 3, 2, 0, 0, 4, 2, 4, 4, 1, 4, 0, 0, 0, 2, 4,
      3, 3, 2, 1, 0, 4, 2, 4, 3, 2, 4, 1, 0, 0, 2, 4,
  };
  private final int[] opcodeTable = new int[]{
      1, 3, 2, 2, 1, 1, 2, 1, 5, 2, 2, 2, 1, 1, 2, 1,
      0, 3, 2, 2, 1, 1, 2, 1, 3, 2, 2, 2, 1, 1, 2, 1,
      2, 3, 2, 2, 1, 1, 2, 1, 2, 2, 2, 2, 1, 1, 2, 1,
      2, 3, 2, 2, 3, 3, 3, 1, 2, 2, 2, 2, 1, 1, 2, 1,
      1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 1,
      1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 1,
      1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 1,
      2, 2, 2, 2, 2, 2, 0, 2, 1, 1, 1, 1, 1, 1, 2, 1,
      1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 1,
      1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 1,
      1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 1,
      1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 1,
      2, 3, 3, 4, 3, 4, 2, 4, 2, 4, 3, 0, 3, 6, 2, 4,
      2, 3, 3, 0, 3, 4, 2, 4, 2, 4, 3, 0, 3, 0, 2, 4,
      3, 3, 2, 0, 0, 4, 2, 4, 4, 1, 4, 0, 0, 0, 2, 4,
      3, 3, 2, 1, 0, 4, 2, 4, 3, 2, 4, 1, 0, 0, 2, 4
  };
  private final int[] cbOpcodeTable = new int[]{
      2, 2, 2, 2, 2, 2, 4, 2, 2, 2, 2, 2, 2, 2, 4, 2,
      2, 2, 2, 2, 2, 2, 4, 2, 2, 2, 2, 2, 2, 2, 4, 2,
      2, 2, 2, 2, 2, 2, 4, 2, 2, 2, 2, 2, 2, 2, 4, 2,
      2, 2, 2, 2, 2, 2, 4, 2, 2, 2, 2, 2, 2, 2, 4, 2,
      2, 2, 2, 2, 2, 2, 3, 2, 2, 2, 2, 2, 2, 2, 3, 2,
      2, 2, 2, 2, 2, 2, 3, 2, 2, 2, 2, 2, 2, 2, 3, 2,
      2, 2, 2, 2, 2, 2, 3, 2, 2, 2, 2, 2, 2, 2, 3, 2,
      2, 2, 2, 2, 2, 2, 3, 2, 2, 2, 2, 2, 2, 2, 3, 2,
      2, 2, 2, 2, 2, 2, 4, 2, 2, 2, 2, 2, 2, 2, 4, 2,
      2, 2, 2, 2, 2, 2, 4, 2, 2, 2, 2, 2, 2, 2, 4, 2,
      2, 2, 2, 2, 2, 2, 4, 2, 2, 2, 2, 2, 2, 2, 4, 2,
      2, 2, 2, 2, 2, 2, 4, 2, 2, 2, 2, 2, 2, 2, 4, 2,
      2, 2, 2, 2, 2, 2, 4, 2, 2, 2, 2, 2, 2, 2, 4, 2,
      2, 2, 2, 2, 2, 2, 4, 2, 2, 2, 2, 2, 2, 2, 4, 2,
      2, 2, 2, 2, 2, 2, 4, 2, 2, 2, 2, 2, 2, 2, 4, 2,
      2, 2, 2, 2, 2, 2, 4, 2, 2, 2, 2, 2, 2, 2, 4, 2,
  };
  public boolean cpuRunning;
  boolean taken;
  boolean usingCB;
  private Memory memory;
  private boolean ime;
  private boolean zero;
  private boolean subtract;
  private boolean halfCarry;
  private boolean carry;
  // Registers
  private int a, b, c, d, e, h, l;
  private char sp, pc;
  private int opcode;
  private boolean pendingDisable;
  private boolean pendingEnable;
  private boolean halted;

  public Z80() {
    usingCB = false;
    cpuRunning = false;
    halted = false;
    ime = false;
    taken = false;
  }

  public int runCycle() {
    if (halted) {
      opcode = 0;
      nop();
      return 4;
    } else {

      opcode = memory.read8(true, pc++);

      if (show) {
        System.out.println(printRegisters());
      }

      switch (opcode) {
        case 0x00:
          nop();
          break;

        case 0x10:
          break;

        case 0x06:
        case 0x0E:
        case 0x16:
        case 0x1E:
        case 0x26:
        case 0x2E:
          ld_nn_n();
          break;

        case 0x76:
          halt();
          break;

        case 0x7F:
        case 0x78:
        case 0x79:
        case 0x7A:
        case 0x7B:
        case 0x7C:
        case 0x7D:
        case 0x40:
        case 0x41:
        case 0x42:
        case 0x43:
        case 0x44:
        case 0x45:
        case 0x47:
        case 0x48:
        case 0x49:
        case 0x4A:
        case 0x4B:
        case 0x4C:
        case 0x4D:
        case 0x4F:
        case 0x50:
        case 0x51:
        case 0x52:
        case 0x53:
        case 0x54:
        case 0x55:
        case 0x58:
        case 0x57:
        case 0x59:
        case 0x5A:
        case 0x5B:
        case 0x5C:
        case 0x5D:
        case 0x5F:
        case 0x60:
        case 0x61:
        case 0x62:
        case 0x63:
        case 0x64:
        case 0x65:
        case 0x67:
        case 0x68:
        case 0x69:
        case 0x6A:
        case 0x6B:
        case 0x6C:
        case 0x6D:
        case 0x6F:
          ld_r1_r2();
          break;

        case 0x46:
        case 0x4E:
        case 0x56:
        case 0x5E:
        case 0x66:
        case 0x6E:
        case 0x7E:
          ld_r_hl();
          break;

        case 0x70:
        case 0x71:
        case 0x72:
        case 0x73:
        case 0x74:
        case 0x75:
        case 0x77:
          ld_hl_r();
          break;

        case 0x0A:
        case 0x1A:
        case 0x2A:
        case 0x3A:
          ld_A_xx();
          break;

        case 0x02:
        case 0x12:
        case 0x22:
        case 0x32:
          ld_xx_A();
          break;

        case 0xC3:
          jp_nn();
          break;

        case 0x0C:
        case 0x04:
        case 0x14:
        case 0x24:
        case 0x1C:
        case 0x2C:
        case 0x3C:
          inc_n();
          break;

        case 0x3D:
        case 0x05:
        case 0x15:
        case 0x1D:
        case 0x25:
        case 0x2D:
        case 0x0D:
          dec_n();
          break;

        case 0x01:
        case 0x11:
        case 0x21:
        case 0x31:
          ld_dd_nn();
          break;

        case 0x20:
        case 0x28:
        case 0x30:
        case 0x38:
          jr_cc_n();
          break;

        case 0xF3:
          di();
          break;

        case 0xEA:
          ld_nn_A();
          break;

        case 0x3E:
          a = getN();
          break;

        case 0xE0:
          ldh_n_A();
          break;

        case 0x18:
          jr_r();
          break;

        case 0xCD:
          call();
          break;

        case 0xC9:
          ret();
          break;

        case 0xF5:
        case 0xC5:
        case 0xD5:
        case 0xE5:
          push_nn();
          break;

        case 0xC1:
        case 0xD1:
        case 0xE1:
        case 0xF1:
          pop_nn();
          break;

        case 0x03:
        case 0x13:
        case 0x23:
        case 0x33:
          inc_nn();
          break;

        case 0xB7:
        case 0xB0:
        case 0xB1:
        case 0xB2:
        case 0xB3:
        case 0xB4:
        case 0xB5:
          or_r();
          break;

        case 0xF0:
          ldh_A_();
          break;

        case 0xBF:
        case 0xB8:
        case 0xB9:
        case 0xBA:
        case 0xBB:
        case 0xBC:
        case 0xBD:
          cp_r();
          break;

        case 0xFE:
          cp_n();
          break;

        case 0xFA:
          LD_A_nn();
          break;

        case 0xA7:
        case 0xA0:
        case 0xA1:
        case 0xA2:
        case 0xA3:
        case 0xA4:
        case 0xA5:
          and_r();
          break;

        case 0xE6:
          and_n();
          break;

        case 0xC4:
        case 0xCC:
        case 0xD4:
        case 0xDC:
          call_cc_nn();
          break;

        case 0xEE:
          xor_n();
          break;

        case 0xAF:
        case 0xA8:
        case 0xA9:
        case 0xAA:
        case 0xAB:
        case 0xAC:
        case 0xAD:
          xor_r();
          break;

        case 0x87:
        case 0x80:
        case 0x81:
        case 0x82:
        case 0x83:
        case 0x84:
        case 0x85:
          add_a_r();
          break;

        case 0xC6:
          add_a_n();
          break;

        case 0xD6:
          sub_n();
          break;

        case 0xAE:
          xor_HL();
          break;

        case 0x1F:
          rra();
          break;

        case 0x8F:
        case 0x88:
        case 0x89:
        case 0x8A:
        case 0x8B:
        case 0x8C:
        case 0x8D:
          adc_A_r();
          break;

        case 0xCE:
          adc_A_n();
          break;

        case 0xC0:
        case 0xC8:
        case 0xD0:
        case 0xD8:
          ret_cc();
          break;

        case 0xB6:
          or_HL();
          break;

        case 0x35:
          dec_HL();
          break;

        case 0x09:
        case 0x19:
        case 0x29:
        case 0x39:
          add_HL_r();
          break;

        case 0xE9:
          jp_HL();
          break;

        case 0x08:
          ld_nn_SP();
          break;

        case 0xF9:
          ld_SP_HL();
          break;

        case 0xC2:
        case 0xCA:
        case 0xD2:
        case 0xDA:
          jp_cc_nn();
          break;

        case 0x27:
          daa();
          break;

        case 0x36:
          ld_hl_n();
          break;

        case 0xF6:
          or_n();
          break;

        case 0x37:
          scf();
          break;

        case 0x0B:
        case 0x1B:
        case 0x2B:
        case 0x3B:
          dec_ss();
          break;

        case 0xBE:
          cp_HL();
          break;

        case 0x86:
          add_A_HL();
          break;

        case 0x8E:
          adc_A_HL();
          break;

        case 0x96:
          sub_HL();
          break;

        case 0x2F:
          cpl();
          break;

        case 0x3F:
          ccf();
          break;

        case 0x97:
        case 0x90:
        case 0x91:
        case 0x92:
        case 0x93:
        case 0x94:
        case 0x95:
          sub_r();
          break;

        case 0x9F:
        case 0x98:
        case 0x99:
        case 0x9A:
        case 0x9B:
        case 0x9C:
        case 0x9D:
          sbc_r();
          break;

        case 0xDE:
          sbc_n();
          break;

        case 0x07:
          rlca();
          break;

        case 0x17:
          rla();
          break;

        case 0x0F:
          rrca();
          break;

        case 0xFB:
          ei();
          break;

        case 0x9E:
          sbc_HL();
          break;

        case 0xA6:
          and_HL();
          break;

        case 0x34:
          inc_HL();
          break;

        case 0xF2:
          ld_A_c();
          break;

        case 0xE2:
          ld_c_A();
          break;

        case 0xE8:
          add_SP_n();
          break;

        case 0xF8:
          ldhl_SP_n();
          break;

        case 0xD9:
          reti();
          break;

        case 0xC7:
        case 0xCF:
        case 0xD7:
        case 0xDF:
        case 0xE7:
        case 0xEF:
        case 0xF7:
        case 0xFF:
          rst_n();
          break;

        case 0xCB:
          opcode = memory.read8(true, pc++);

          usingCB = true;

          switch (opcode) {

            case 0x3F:
            case 0x38:
            case 0x39:
            case 0x3A:
            case 0x3B:
            case 0x3C:
            case 0x3D:
              srl_r();
              break;

            case 0x1F:
            case 0x18:
            case 0x19:
            case 0x1A:
            case 0x1B:
            case 0x1C:
            case 0x1D:
              rr_r();
              break;

            case 0x37:
            case 0x30:
            case 0x31:
            case 0x32:
            case 0x33:
            case 0x34:
            case 0x35:
              swap_r();
              break;

            case 0x00:
            case 0x07:
            case 0x01:
            case 0x02:
            case 0x03:
            case 0x04:
            case 0x05:
              rlc_r();
              break;

            case 0x0F:
            case 0x08:
            case 0x09:
            case 0x0A:
            case 0x0B:
            case 0x0C:
            case 0x0D:
              rrc_r();
              break;

            case 0x17:
            case 0x10:
            case 0x11:
            case 0x12:
            case 0x13:
            case 0x14:
            case 0x15:
              rl_r();
              break;

            case 0x27:
            case 0x20:
            case 0x21:
            case 0x22:
            case 0x23:
            case 0x24:
            case 0x25:
              sla_r();
              break;

            case 0x28:
            case 0x29:
            case 0x2A:
            case 0x2B:
            case 0x2C:
            case 0x2D:
            case 0x2F:
              sra_r();
              break;

            case 0x47:
            case 0x40:
            case 0x41:
            case 0x42:
            case 0x43:
            case 0x44:
            case 0x45:
            case 0x48:
            case 0x49:
            case 0x4A:
            case 0x4B:
            case 0x4C:
            case 0x4D:
            case 0x4F:
            case 0x57:
            case 0x50:
            case 0x51:
            case 0x52:
            case 0x53:
            case 0x54:
            case 0x55:
            case 0x58:
            case 0x59:
            case 0x5A:
            case 0x5B:
            case 0x5C:
            case 0x5D:
            case 0x5F:

            case 0x60:
            case 0x61:
            case 0x62:
            case 0x63:
            case 0x64:
            case 0x65:
            case 0x67:
            case 0x68:
            case 0x69:
            case 0x6A:
            case 0x6B:
            case 0x6C:
            case 0x6D:
            case 0x6F:

            case 0x70:
            case 0x71:
            case 0x72:
            case 0x73:
            case 0x74:
            case 0x75:
            case 0x77:
            case 0x78:
            case 0x79:
            case 0x7A:
            case 0x7B:
            case 0x7C:
            case 0x7D:
            case 0x7F:
              bit_b_r();
              break;

            case 0x80:
            case 0x81:
            case 0x82:
            case 0x83:
            case 0x84:
            case 0x85:
            case 0x87:
            case 0x88:
            case 0x89:
            case 0x8A:
            case 0x8B:
            case 0x8C:
            case 0x8D:
            case 0x8F:

            case 0x90:
            case 0x91:
            case 0x92:
            case 0x93:
            case 0x94:
            case 0x95:
            case 0x97:
            case 0x98:
            case 0x99:
            case 0x9A:
            case 0x9B:
            case 0x9C:
            case 0x9D:
            case 0x9F:

            case 0xA0:
            case 0xA1:
            case 0xA2:
            case 0xA3:
            case 0xA4:
            case 0xA5:
            case 0xA7:
            case 0xA8:
            case 0xA9:
            case 0xAA:
            case 0xAB:
            case 0xAC:
            case 0xAD:
            case 0xAF:

            case 0xB0:
            case 0xB1:
            case 0xB2:
            case 0xB3:
            case 0xB4:
            case 0xB5:
            case 0xB7:
            case 0xB8:
            case 0xB9:
            case 0xBA:
            case 0xBB:
            case 0xBC:
            case 0xBD:
            case 0xBF:
              res_b_r();
              break;

            case 0xC0:
            case 0xC1:
            case 0xC2:
            case 0xC3:
            case 0xC4:
            case 0xC5:
            case 0xC7:
            case 0xC8:
            case 0xC9:
            case 0xCA:
            case 0xCB:
            case 0xCC:
            case 0xCD:
            case 0xCF:

            case 0xD0:
            case 0xD1:
            case 0xD2:
            case 0xD3:
            case 0xD4:
            case 0xD5:
            case 0xD7:
            case 0xD8:
            case 0xD9:
            case 0xDA:
            case 0xDB:
            case 0xDC:
            case 0xDD:
            case 0xDF:

            case 0xE0:
            case 0xE1:
            case 0xE2:
            case 0xE3:
            case 0xE4:
            case 0xE5:
            case 0xE7:
            case 0xE8:
            case 0xE9:
            case 0xEA:
            case 0xEB:
            case 0xEC:
            case 0xED:
            case 0xEF:

            case 0xF0:
            case 0xF1:
            case 0xF2:
            case 0xF3:
            case 0xF4:
            case 0xF5:
            case 0xF7:
            case 0xF8:
            case 0xF9:
            case 0xFA:
            case 0xFB:
            case 0xFC:
            case 0xFD:
            case 0xFF:
              set_b_r();
              break;

            case 0x06:
              rlc_HL();
              break;

            case 0x0E:
              rrc_HL();
              break;

            case 0x16:
              rl_HL();
              break;

            case 0x1E:
              rr_HL();
              break;

            case 0x26:
              sla_HL();
              break;

            case 0x2E:
              sra_HL();
              break;

            case 0x36:
              swap_HL();
              break;

            case 0x3E:
              srl_HL();
              break;

            case 0x46:
            case 0x4E:
            case 0x56:
            case 0x5E:
            case 0x66:
            case 0x6E:
            case 0x76:
            case 0x7E:
              bit_b_HL();
              break;

            case 0x86:
            case 0x8E:
            case 0x96:
            case 0x9E:
            case 0xA6:
            case 0xAE:
            case 0xB6:
            case 0xBE:
              res_b_HL();
              break;

            case 0xC6:
            case 0xCE:
            case 0xD6:
            case 0xDE:
            case 0xE6:
            case 0xEE:
            case 0xF6:
            case 0xFE:
              set_b_HL();
              break;

            default:
              System.out.println("Unimplemented CB Opcode " + Integer.toHexString(opcode));
              System.out.println(printRegisters());
              System.exit(0);
              return 0;
          }
          break;

        default:
          System.out.println("Unimplemented Opcode " + Integer.toHexString(opcode));
          System.out.println(printRegisters());
          return 0;
      }

      if (taken) {
        taken = false;
        return conditionalTakenOpcodeTable[opcode] * 4;
      } else if (usingCB) {
        usingCB = false;
        return cbOpcodeTable[opcode] * 4;
      } else {
        return opcodeTable[opcode] * 4;
      }
    }
  }

  public void checkIme() {

    if (pendingDisable) {
      if (memory.read8(true, pc - 2) != 0xF3) {
        pendingDisable = false;
        ime = false;
      }
    }

    if (pendingEnable) {
      if (memory.read8(true, pc - 2) != 0xFB) {
        pendingEnable = false;
        ime = true;
      }
    }

  }

  public void tickTimer() {
    memory.timerVariable++;
    memory.dividerVariable++;

    int timerControl = memory.io[0xFF07 - 0xFF00];

    if (((timerControl >>> 2) & 1) != 0) {
      if (memory.timerVariable >= memory.currentClockSpeed) {
        memory.timerVariable = 0;

        memory.io[0xFF05 - 0xFF00]++;

        if (memory.io[0xFF05 - 0xFF00] > 0xFF) {
          memory.io[0xFF05 - 0xFF00] = memory.io[0xFF06 - 0xFF00];
          requestInterrupt(2);
        }
      }
    }

    if (memory.dividerVariable >= 265) {
      memory.io[0xFF04 - 0xFF00]++;
      if (memory.io[0xFF04 - 0xFF00] == 256) {
        memory.io[0xFF04 - 0xFF00] = 0;
      }
      memory.dividerVariable = 0;
    }
  }

  private void nop() {

  }

  private void ld_nn_n() {
    int n = getN();

    switch ((opcode >> 3) & 0x7) {
      case 0x7:
        a = n;
        break;

      case 0x0:
        b = n;
        break;

      case 0x1:
        c = n;
        break;

      case 0x2:
        d = n;
        break;

      case 0x3:
        e = n;
        break;

      case 0x4:
        h = n;
        break;

      case 0x5:
        l = n;
        break;

      default:
        System.out.println("Unknown Operation " + Integer.toHexString(opcode));
        break;
    }

  }

  private void ld_r1_r2() {
    int r2 = 0;

    switch (opcode & 0x7) {
      case 0x7:
        r2 = a;
        break;

      case 0x0:
        r2 = b;
        break;

      case 0x1:
        r2 = c;
        break;

      case 0x2:
        r2 = d;
        break;

      case 0x3:
        r2 = e;
        break;

      case 0x4:
        r2 = h;
        break;

      case 0x5:
        r2 = l;
        break;

      default:
        System.out.println("Unknown Operation " + Integer.toHexString(opcode));
        break;
    }

    switch ((opcode >> 3) & 0x7) {
      case 0x7:
        a = r2;
        break;

      case 0x0:
        b = r2;
        break;

      case 0x1:
        c = r2;
        break;

      case 0x2:
        d = r2;
        break;

      case 0x3:
        e = r2;
        break;

      case 0x4:
        h = r2;
        break;

      case 0x5:
        l = r2;
        break;

      default:
        System.out.println("Unknown Operation " + Integer.toHexString(opcode));
        break;
    }
  }

  private void ld_r_hl() {
    int nn = memory.read8(true, h << 8 | l);

    switch ((opcode >> 3) & 0x7) {
      case 0x7:
        a = nn;
        break;

      case 0x0:
        b = nn;
        break;

      case 0x1:
        c = nn;
        break;

      case 0x2:
        d = nn;
        break;

      case 0x3:
        e = nn;
        break;

      case 0x4:
        h = nn;
        break;

      case 0x5:
        l = nn;
        break;

      default:
        System.out.println("Unknown Operation " + Integer.toHexString(opcode));
        break;
    }
  }

  private void ld_hl_r() {
    int data = 0;

    switch (opcode & 7) {
      case 0x7:
        data = a;
        break;

      case 0x0:
        data = b;
        break;

      case 0x1:
        data = c;
        break;

      case 0x2:
        data = d;
        break;

      case 0x3:
        data = e;
        break;

      case 0x4:
        data = h;
        break;

      case 0x5:
        data = l;
        break;

      default:
        System.out.println("Unknown Operation " + Integer.toHexString(opcode));
        break;
    }

    memory.write8(true, h << 8 | l, (char) data);
  }

  private void ld_hl_n() {
    int n = getN();
    memory.write8(true, h << 8 | l, (char) n);
  }

  private void ld_A_xx() {

    switch (opcode) {
      case 0x0A:
        a = memory.read8(true, b << 8 | c);
        break;

      case 0x1A:
        a = memory.read8(true, d << 8 | e);
        break;

      case 0x2A: {
        char hl = (char) (h << 8 | l);
        a = memory.read8(true, hl);
        hl += 1;
        h = (hl >> 8) & 0xFF;
        l = hl & 0xFF;
        break;
      }

      case 0x3A: {
        char hl = (char) (h << 8 | l);
        a = memory.read8(true, hl);
        hl -= 1;
        h = (hl >> 8) & 0xFF;
        l = hl & 0xFF;
        break;
      }
    }
  }

  private void ld_xx_A() {
    switch (opcode) {
      case 0x2:
        memory.write8(true, b << 8 | c, a);
        break;

      case 0x12:
        memory.write8(true, d << 8 | e, a);
        break;

      case 0x22: {
        char hl = (char) (h << 8 | l);
        memory.write8(true, hl, a);
        hl += 1;
        h = (hl >> 8) & 0xFF;
        l = hl & 0xFF;
        break;
      }

      case 0x32: {
        char hl = (char) (h << 8 | l);
        memory.write8(true, hl, a);
        hl -= 1;
        h = (hl >> 8) & 0xFF;
        l = hl & 0xFF;
        break;
      }
    }
  }

  private void ld_dd_nn() {
    char nn = getNN();

    switch ((opcode >> 4) & 0x3) {
      case 0x00:
        b = (nn >> 8) & 0xFF;
        c = nn & 0xFF;
        break;

      case 0x01:
        d = (nn >> 8) & 0xFF;
        e = nn & 0xFF;
        break;

      case 0x02:
        h = (nn >> 8) & 0xFF;
        l = nn & 0xFF;
        break;

      case 0x03:
        sp = nn;
        break;
    }
  }

  private void jp_nn() {
    pc = getNN();
  }

  private void inc_n() {
    int result = 0;
    int n = 0;
    switch ((opcode >> 3) & 0x7) {
      case 0x7:
        n = a;
        a += 1;
        result = a;
        a &= 0xFF;
        break;

      case 0x0:
        n = b;
        b += 1;
        result = b;
        b &= 0xFF;
        break;

      case 0x1:
        n = c;
        c += 1;
        result = c;
        c &= 0xFF;
        break;

      case 0x2:
        n = d;
        d += 1;
        result = d;
        d &= 0xFF;
        break;

      case 0x3:
        n = e;
        e += 1;
        result = e;
        e &= 0xFF;
        break;

      case 0x4:
        n = h;
        h += 1;
        result = h;
        h &= 0xFF;
        break;

      case 0x5:
        n = l;
        l += 1;
        result = l;
        l &= 0xFF;
        break;

      default:
        System.out.println("Unknown Operation " + Integer.toHexString(opcode));
        break;
    }

    zero = (result & 0xFF) == 0;
    subtract = false;

    boolean prevBit3 = ((n >> 3) & 1) != 0;
    boolean afterBit3 = ((result >> 3) & 1) != 0;

    halfCarry = prevBit3 && !afterBit3;
  }

  private void jr_cc_n() {
    byte n = (byte) getN();

    switch ((opcode >> 3) & 0x3) {
      case 0:
        if (!zero) {
          pc += n;
          taken = true;
        }
        break;

      case 1:
        if (zero) {
          pc += n;
          taken = true;
        }
        break;

      case 2:
        if (!carry) {
          pc += n;
          taken = true;
        }
        break;

      case 3:
        if (carry) {
          pc += n;
          taken = true;
        }
        break;
    }
  }

  private void jp_cc_nn() {
    char n = getNN();

    switch ((opcode >> 3) & 0x3) {
      case 0:
        if (!zero) {
          pc = n;
          taken = true;
        }
        break;

      case 1:
        if (zero) {
          pc = n;
          taken = true;
        }
        break;

      case 2:
        if (!carry) {
          pc = n;
          taken = true;
        }
        break;

      case 3:
        if (carry) {
          pc = n;
          taken = true;
        }
        break;

    }
  }

  private void dec_n() {
    int result = 0;
    int n = 0;

    switch ((opcode >> 3) & 0x7) {
      case 0x7:
        n = a;
        a -= 1;
        result = a;
        a &= 0xFF;
        break;

      case 0x0:
        n = b;
        b -= 1;
        result = b;
        b &= 0xFF;
        break;

      case 0x1:
        n = c;
        c -= 1;
        result = c;
        c &= 0xFF;
        break;

      case 0x2:
        n = d;
        d -= 1;
        result = d;
        d &= 0xFF;
        break;

      case 0x3:
        n = e;
        e -= 1;
        result = e;
        e &= 0xFF;
        break;

      case 0x4:
        n = h;
        h -= 1;
        result = h;
        h &= 0xFF;
        break;

      case 0x5:
        n = l;
        l -= 1;
        result = l;
        l &= 0xFF;
        break;

      default:
        System.out.println("Unknown Operation " + Integer.toHexString(opcode));
        break;
    }

    zero = (result & 0xFF) == 0;
    subtract = true;
    halfCarry = (n & 0xF) < (result & 0xF);
  }

  private void dec_HL() {
    int result = 0;
    int data = memory.read8(true, h << 8 | l);
    result = data - 1;

    zero = (result & 0xFF) == 0;
    subtract = true;
    halfCarry = ((result ^ 1 ^ data) & 0x10) == 0x10;
    memory.write8(true, h << 8 | l, result);
  }

  private void inc_HL() {
    int result = 0;
    int data = memory.read8(true, h << 8 | l);
    boolean b4 = ((data >> 3) & 1) != 0;
    result = (data + 1) & 0xFF;
    boolean bA = ((result >> 3) & 1) != 0;

    zero = (result & 0xFF) == 0;
    subtract = false;
    halfCarry = b4 && !bA;
    memory.write8(true, h << 8 | l, result & 0xFF);
  }

  private void di() {
    pendingDisable = true;
  }

  private void ld_nn_A() {
    char nn = getNN();
    memory.write8(true, nn, a);
  }

  private void ldh_n_A() {
    int n = getN();
    memory.write8(true, 0xFF00 + n, a);
  }

  private void call() {
    char nn = getNN();
    push(pc);
    pc = nn;
  }

  private void call_cc_nn() {
    char nn = getNN();

    switch ((opcode >> 3) & 0x3) {
      case 0:
        if (!zero) {
          push(pc);
          pc = nn;
          taken = true;
        }
        break;

      case 1:
        if (zero) {
          push(pc);
          pc = nn;
          taken = true;
        }
        break;

      case 2:
        if (!carry) {
          push(pc);
          pc = nn;
          taken = true;
        }
        break;

      case 3:
        if (carry) {
          push(pc);
          pc = nn;
          taken = true;
        }
        break;

      default:
        System.out.println("UNKNOWN CALL CC NN");
        break;
    }
  }

  private void jr_r() {
    byte n = (byte) getN();
    pc += n;
  }

  private void ret() {
    pc = pop();
  }

  private void push_nn() {
    char data = 0;

    switch ((opcode >> 4) & 0x3) {
      case 0:
        data = (char) ((b << 8) | c);
        break;

      case 1:
        data = (char) ((d << 8) | e);
        break;

      case 2:
        data = (char) ((h << 8) | l);
        break;

      case 3:
        data = (char) ((a << 8) | getFlags());
        break;

    }

    push(data);
  }

  private void pop_nn() {
    char data = pop();

    switch ((opcode >> 4) & 0x3) {
      case 0:
        b = data >> 8;
        c = data & 0xFF;
        break;

      case 1:
        d = data >> 8;
        e = data & 0xFF;
        break;

      case 2:
        h = data >> 8;
        l = data & 0xFF;
        break;

      case 3:
        a = data >> 8;
        zero = ((data >> 7) & 1) != 0;
        subtract = ((data >> 6) & 1) != 0;
        halfCarry = ((data >> 5) & 1) != 0;
        carry = ((data >> 4) & 1) != 0;
        break;
    }
  }

  private void or_r() {
    int result = 0;

    switch (opcode & 0x7) {
      case 0:
        result = a | b;
        break;

      case 1:
        result = a | c;
        break;

      case 2:
        result = a | d;
        break;

      case 3:
        result = a | e;
        break;

      case 4:
        result = a | h;
        break;

      case 5:
        result = a | l;
        break;

      case 7:
        result = a;
        break;
    }

    a = result & 0xFF;
    zero = a == 0;
    subtract = false;
    halfCarry = false;
    carry = false;


  }

  private void or_HL() {
    int result = 0;

    int data = memory.read8(true, h << 8 | l);

    result = a | data;

    a = result & 0xFF;
    zero = a == 0;
    subtract = false;
    halfCarry = false;
    carry = false;
  }

  private void or_n() {
    int result = 0;

    result = getN() | a;

    a = result & 0xFF;
    zero = a == 0;
    subtract = false;
    halfCarry = false;
    carry = false;

  }

  private void ldh_A_() {
    int n = getN();
    a = memory.read8(true, 0xFF00 + n);
  }

  private void inc_nn() {
    char data;
    switch ((opcode >> 4) & 0x3) {
      case 0:
        data = (char) ((b << 8) | c);
        data += 1;
        b = (data >> 8) & 0xFF;
        c = data & 0xFF;
        break;

      case 1:
        data = (char) ((d << 8) | e);
        data += 1;
        d = (data >> 8) & 0xFF;
        e = data & 0xFF;
        break;

      case 2:
        data = (char) ((h << 8) | l);
        data += 1;
        h = (data >> 8) & 0xFF;
        l = data & 0xFF;
        break;

      case 3:
        sp += 1;
        break;
    }
  }

  private void cp_r() {
    int result = 0;
    int n = 0;

    switch (opcode & 0x7) {
      case 0x7:
        n = a;
        result = 0;
        break;

      case 0x0:
        n = b;
        result = a - b;
        break;

      case 0x1:
        n = c;
        result = a - c;
        break;

      case 0x2:
        n = d;
        result = a - d;
        break;

      case 0x3:
        n = e;
        result = a - e;
        break;

      case 0x4:
        n = h;
        result = a - h;
        break;

      case 0x5:
        n = l;
        result = a - l;
        break;

      default:
        System.out.println("Unknown Operation " + Integer.toHexString(opcode));
        break;
    }

    zero = ((result & 0xFF) == 0);
    subtract = true;
    halfCarry = ((a & 0xF) < (n & 0xF));
    carry = a < n;
  }

  private void cp_n() {
    int result = 0;
    int n = getN();

    result = a - n;

    zero = ((result & 0xFF) == 0);
    subtract = true;
    halfCarry = ((a & 0xF) < (n & 0xF));
    carry = a < n;
  }

  private void cp_HL() {
    int n = memory.read8(true, (h << 8 | l));

    int result = a - n;

    zero = ((result & 0xFF) == 0);
    subtract = true;
    halfCarry = ((a & 0xF) < (n & 0xF));
    carry = a < n;
  }

  private void sub_n() {
    int result = 0;
    int n = getN();

    result = a - n;

    zero = ((result & 0xFF) == 0);
    subtract = true;
    halfCarry = ((a & 0x0F) < (n & 0x0F));
    carry = (a & 0xFF) < (n & 0xFF);

    a = result & 0xFF;
  }

  private void sub_HL() {
    int result = 0;
    int n = memory.read8(true, h << 8 | l);

    result = a - n;

    zero = ((result & 0xFF) == 0);
    subtract = true;
    halfCarry = ((a & 0x0F) < (n & 0x0F));
    carry = (a & 0xFF) < (n & 0xFF);

    a = result & 0xFF;
  }

  private void LD_A_nn() {
    int nn = getNN();
    a = memory.read8(true, nn);
  }

  private void and_r() {
    int result = 0;

    switch (opcode & 0x7) {
      case 0x7:
        result = a;
        break;

      case 0x0:
        result = a & b;
        break;

      case 0x1:
        result = a & c;
        break;

      case 0x2:
        result = a & d;
        break;

      case 0x3:
        result = a & e;
        break;

      case 0x4:
        result = a & h;
        break;

      case 0x5:
        result = a & l;
        break;

      default:
        System.out.println("Unknown Operation " + Integer.toHexString(opcode));
        break;
    }

    a = result & 0xFF;

    zero = a == 0;
    subtract = false;
    halfCarry = true;
    carry = false;
  }

  private void and_n() {
    int result = 0;

    result = a & getN();

    a = result & 0xFF;

    zero = a == 0;
    subtract = false;
    halfCarry = true;
    carry = false;
  }

  private void and_HL() {
    int result = 0;

    result = a & memory.read8(true, h << 8 | l);

    a = result & 0xFF;

    zero = a == 0;
    subtract = false;
    halfCarry = true;
    carry = false;
  }

  private void xor_r() {
    int result = 0;

    switch (opcode & 0x7) {
      case 0x7:
        result = 0;
        break;

      case 0x0:
        result = a ^ b;
        break;

      case 0x1:
        result = a ^ c;
        break;

      case 0x2:
        result = a ^ d;
        break;

      case 0x3:
        result = a ^ e;
        break;

      case 0x4:
        result = a ^ h;
        break;

      case 0x5:
        result = a ^ l;
        break;

      default:
        System.out.println("Unknown Operation " + Integer.toHexString(opcode));
        break;
    }

    a = result & 0xFF;

    zero = a == 0;
    subtract = false;
    halfCarry = false;
    carry = false;
  }

  private void xor_n() {
    int result = a ^ getN();

    a = result & 0xFF;

    zero = a == 0;
    subtract = false;
    halfCarry = false;
    carry = false;
  }

  private void xor_HL() {
    int result = a ^ memory.read8(true, (h << 8) | l);

    a = result & 0xFF;

    zero = a == 0;
    subtract = false;
    halfCarry = false;
    carry = false;
  }

  private void add_a_r() {
    int result = 0;
    int n = 0;

    switch (opcode & 0x7) {
      case 0x7:
        n = a;
        break;

      case 0x0:
        n = b;
        break;

      case 0x1:
        n = c;
        break;

      case 0x2:
        n = d;
        break;

      case 0x3:
        n = e;
        break;

      case 0x4:
        n = h;
        break;

      case 0x5:
        n = l;
        break;

      default:
        System.out.println("Unknown Operation " + Integer.toHexString(opcode));
        break;
    }

    result = (a + n) & 0xFF;

    zero = result == 0;
    subtract = false;
    halfCarry = ((result ^ n ^ a) & 0x10) == 0x10;
    carry = result < a;

    a = result & 0xFF;
  }

  private void add_a_n() {
    int result = 0;
    int n = getN();

    result = (a + n) & 0xFF;

    zero = result == 0;
    subtract = false;
    halfCarry = ((result ^ n ^ a) & 0x10) == 0x10;
    carry = result < a;

    a = result & 0xFF;
  }

  private void srl_r() {
    int result = 0;

    switch (opcode & 0x7) {
      case 0x7:
        carry = (a & 1) != 0;
        result = a >>> 1;
        a = result & 0xFF;
        break;

      case 0x0:
        carry = (b & 1) != 0;
        result = b >>> 1;
        b = result & 0xFF;
        break;

      case 0x1:
        carry = (c & 1) != 0;
        result = c >>> 1;
        c = result & 0xFF;
        break;

      case 0x2:
        carry = (d & 1) != 0;
        result = d >>> 1;
        d = result & 0xFF;
        break;

      case 0x3:
        carry = (e & 1) != 0;
        result = e >>> 1;
        e = result & 0xFF;
        break;

      case 0x4:
        carry = (h & 1) != 0;
        result = h >> 1;
        h = result & 0xFF;
        break;

      case 0x5:
        carry = (l & 1) != 0;
        result = l >> 1;
        l = result & 0xFF;
        break;

      default:
        System.out.println("Unknown Operation " + Integer.toHexString(opcode));
        break;
    }

    zero = (result & 0xFF) == 0;
    subtract = false;
    halfCarry = false;
  }

  private void srl_HL() {
    int result = 0;
    int a = memory.read8(true, h << 8 | l);
    carry = (a & 1) != 0;
    result = a >>> 1;
    a = result & 0xFF;

    memory.write8(true, h << 8 | l, a);

    zero = (result & 0xFF) == 0;
    subtract = false;
    halfCarry = false;
  }

  private void rr_r() {
    boolean oldCarry = carry;
    int result = 0;

    switch (opcode & 0x7) {
      case 0x7:
        carry = (a & 1) != 0;
        result = a >>> 1;

        if (oldCarry) {
          result |= (1 << 7);
        } else {
          result &= ~(1 << 7);
        }

        a = result & 0xFF;
        break;

      case 0x0:
        carry = (b & 1) != 0;
        result = b >>> 1;
        if (oldCarry) {
          result |= 1 << 7;
        } else {
          result &= ~(1 << 7);
        }

        b = result & 0xFF;
        break;

      case 0x1:
        carry = (c & 1) != 0;
        result = c >>> 1;
        if (oldCarry) {
          result |= 1 << 7;
        } else {
          result &= ~(1 << 7);
        }

        c = result & 0xFF;
        break;

      case 0x2:
        carry = (d & 1) != 0;
        result = d >>> 1;
        if (oldCarry) {
          result |= 1 << 7;
        } else {
          result &= ~(1 << 7);
        }

        d = result & 0xFF;
        break;

      case 0x3:
        carry = (e & 1) != 0;
        result = e >>> 1;
        if (oldCarry) {
          result |= 1 << 7;
        } else {
          result &= ~(1 << 7);
        }

        e = result & 0xFF;
        break;

      case 0x4:
        carry = (h & 1) != 0;
        result = h >> 1;
        if (oldCarry) {
          result |= 1 << 7;
        } else {
          result &= ~(1 << 7);
        }

        h = result & 0xFF;
        break;

      case 0x5:
        carry = (l & 1) != 0;
        result = l >> 1;
        if (oldCarry) {
          result |= 1 << 7;
        } else {
          result &= ~(1 << 7);
        }

        l = result & 0xFF;
        break;

      default:
        System.out.println("Unknown Operation " + Integer.toHexString(opcode));
        break;
    }

    zero = result == 0;
    subtract = false;
    halfCarry = false;
  }

  private void rra() {
    boolean oldCarry = carry;
    int result = 0;
    carry = (a & 1) != 0;
    result = a >>> 1;

    if (oldCarry) {
      result |= 1 << 7;
    } else {
      result &= ~(1 << 7);
    }

    a = result & 0xFF;

    zero = false;
    subtract = false;
    halfCarry = false;
  }

  private void ei() {
    pendingEnable = true;
  }

  private void adc_A_r() {
    int result = 0;
    int n = 0;

    switch (opcode & 0x7) {
      case 0x7:
        n = a;
        break;

      case 0x0:
        n = b;
        break;

      case 0x1:
        n = c;
        break;

      case 0x2:
        n = d;
        break;

      case 0x3:
        n = e;
        break;

      case 0x4:
        n = h;
        break;

      case 0x5:
        n = l;
        break;

      default:
        System.out.println("Unknown Operation " + Integer.toHexString(opcode));
        break;
    }

    result = a + n + (carry ? 1 : 0);

    zero = (result & 0xFF) == 0;
    subtract = false;
    halfCarry = (((a & 0xf) + (n & 0xf) + (carry ? 1 : 0)) & 0x10) == 0x10;
    carry = (((a & 0xFF) + (n & 0xFF) + (carry ? 1 : 0)) & 0x100) == 0x100;

    a = result & 0xFF;
  }

  private void adc_A_n() {
    int result = 0;
    int n = getN();

    result = a + n + (carry ? 1 : 0);

    zero = (result & 0xFF) == 0;
    subtract = false;
    halfCarry = (((a & 0xf) + (n & 0xf) + (carry ? 1 : 0)) & 0x10) == 0x10;
    carry = (((a & 0xFF) + (n & 0xFF) + (carry ? 1 : 0)) & 0x100) == 0x100;

    a = result & 0xFF;
  }

  private void adc_A_HL() {
    int result = 0;
    int n = memory.read8(true, h << 8 | l);

    result = a + n + (carry ? 1 : 0);

    zero = (result & 0xFF) == 0;
    subtract = false;
    halfCarry = (((a & 0xf) + (n & 0xf) + (carry ? 1 : 0)) & 0x10) == 0x10;
    carry = (((a & 0xFF) + (n & 0xFF) + (carry ? 1 : 0)) & 0x100) == 0x100;

    a = result & 0xFF;
  }

  private void ret_cc() {
    boolean flag = false;

    switch ((opcode >> 3) & 0x03) {
      case 0x00:
        flag = !zero;
        break;
      case 0x01:
        flag = zero;
        break;
      case 0x02:
        flag = !carry;
        break;
      case 0x03:
        flag = carry;
        break;
    }

    if (flag) {
      taken = true;
      pc = pop();
    }
  }

  private void add_HL_r() {
    char result = 0;
    char n = 0;
    char hl = (char) (h << 8 | l);

    switch ((opcode >> 4) & 0x3) {
      case 0:
        n = (char) (b << 8 | c);
        break;

      case 1:
        n = (char) (d << 8 | e);
        break;

      case 2:
        n = (char) (h << 8 | l);
        break;

      case 3:
        n = sp;
        break;
    }

    result = (char) (hl + n);

    subtract = false;
    halfCarry = (((hl & 0xFFF) + (n & 0xFFF)) & 0x1000) == 0x1000;
    carry = (hl + n) > 65535;

    h = (result >> 8) & 0xFF;
    l = result & 0xFF;
  }

  private void halt() {
    halted = true;
  }

  // DOCUMENTATION GETS THIS WRONG IT ISN'T MEMORY ITS THE REGISTER
  private void jp_HL() {
    pc = (char) ((h << 8) | l);
  }

  private void ld_nn_SP() {
    char nn = getNN();
    memory.write8(true, nn + 1, (sp >> 8) & 0xFF);
    memory.write8(true, nn, sp & 0xFF);
  }

  private void ld_SP_HL() {
    sp = (char) ((h << 8) | l);
  }

  private void daa() {
    a &= 0xFF; // 0xff to be sure
    if (!subtract) {
      if (halfCarry || ((a & 0xF) > 9)) {
        a += 0x06;
      }

      if (carry || (a > 0x9f)) {
        a += 0x60;
      }
    } else {
      if (halfCarry) {
        a = (a - 0x06) & 0xFF;
      }

      if (carry) {
        a -= 0x60;
      }
    }

    halfCarry = (false);

    if ((a & 0x100) == 0x100) {
      carry = (true);
    }

    a &= 0xFF;

    zero = (a == 0x00);
  }

  private void scf() {
    carry = true;
    subtract = false;
    halfCarry = false;
  }

  private void dec_ss() {
    char data;
    switch ((opcode >> 4) & 0x3) {
      case 0:
        data = (char) ((b << 8) | c);
        data -= 1;
        b = (data >> 8) & 0xFF;
        c = data & 0xFF;
        break;

      case 1:
        data = (char) ((d << 8) | e);
        data -= 1;
        d = (data >> 8) & 0xFF;
        e = data & 0xFF;
        break;

      case 2:
        data = (char) ((h << 8) | l);
        data -= 1;
        h = (data >> 8) & 0xFF;
        l = data & 0xFF;
        break;

      case 3:
        sp -= 1;
        break;
    }
  }

  private void add_A_HL() {
    int result = a + memory.read8(true, h << 8 | l);
    int n = memory.read8(true, h << 8 | l);

    zero = (result & 0xFF) == 0;
    subtract = false;
    halfCarry = (((a & 0xf) + (n & 0xf)) & 0x10) == 0x10;
    carry = (((a & 0xFF) + (n & 0xFF)) & 0x100) == 0x100;

    a = result & 0xFF;
  }

  private void cpl() {
    a ^= 0xFF;
    subtract = true;
    halfCarry = true;
  }

  private void swap_r() {
    int result = 0;
    switch (opcode & 0x7) {
      case 0x7:
        a = ((a & 0x0F) << 4) | ((a & 0xF0) >> 4);
        result = a;
        break;

      case 0x0:
        b = ((b & 0x0F) << 4) | ((b & 0xF0) >> 4);
        result = b;
        break;

      case 0x1:
        c = ((c & 0x0F) << 4) | ((c & 0xF0) >> 4);
        result = c;
        break;

      case 0x2:
        d = ((d & 0x0F) << 4) | ((d & 0xF0) >> 4);
        result = d;
        break;

      case 0x3:
        e = ((e & 0x0F) << 4) | ((e & 0xF0) >> 4);
        result = e;
        break;

      case 0x4:
        h = ((h & 0x0F) << 4) | ((h & 0xF0) >> 4);
        result = h;
        break;

      case 0x5:
        l = ((l & 0x0F) << 4) | ((l & 0xF0) >> 4);
        result = l;
        break;

      default:
        System.out.println("Unknown Operation " + Integer.toHexString(opcode));
        break;
    }

    zero = result == 0;
    subtract = false;
    halfCarry = false;
    carry = false;
  }

  private void swap_HL() {
    int result;
    int a = memory.read8(true, h << 8 | l);

    a = ((a & 0x0F) << 4) | ((a & 0xF0) >> 4);
    result = a;

    zero = result == 0;
    subtract = false;
    halfCarry = false;
    carry = false;

    memory.write8(true, h << 8 | l, result);
  }

  private void ccf() {
    subtract = false;
    halfCarry = false;
    carry = !carry;
  }

  private void sub_r() {
    int result = 0;
    int n = 0;

    switch (opcode & 0x7) {
      case 0x7:
        n = a;
        break;

      case 0x0:
        n = b;
        break;

      case 0x1:
        n = c;
        break;

      case 0x2:
        n = d;
        break;

      case 0x3:
        n = e;
        break;

      case 0x4:
        n = h;
        break;

      case 0x5:
        n = l;
        break;

      default:
        System.out.println("Unknown Operation " + Integer.toHexString(opcode));
        break;
    }

    result = (a - n) & 0xFF;

    zero = result == 0;
    subtract = true;
    halfCarry = (a & 0x0F) < (n & 0x0F);
    carry = (a & 0xFF) < (n & 0xFF);

    a = result;
  }

  private void sbc_r() {
    int result = 0;
    int n = 0;

    switch (opcode & 0x7) {
      case 0x7:
        n = a;
        break;

      case 0x0:
        n = b;
        break;

      case 0x1:
        n = c;
        break;

      case 0x2:
        n = d;
        break;

      case 0x3:
        n = e;
        break;

      case 0x4:
        n = h;
        break;

      case 0x5:
        n = l;
        break;

      default:
        System.out.println("Unknown Operation " + Integer.toHexString(opcode));
        break;
    }

    result = ((a - n) - (carry ? 1 : 0));

    zero = (result & 0xFF) == 0;
    subtract = true;
    carry = result < 0;
    result &= 0xFF;
    halfCarry = ((result ^ n ^ a) & 0x10) == 0x10;

    a = result;
  }

  private void sbc_n() {
    int result = 0;
    int n = getN();

    result = ((a - n) - (carry ? 1 : 0));

    zero = (result & 0xFF) == 0;
    subtract = true;
    carry = result < 0;
    result &= 0xFF;
    halfCarry = ((result ^ n ^ a) & 0x10) == 0x10;

    a = result;
  }

  private void sbc_HL() {
    int result = 0;
    int n = memory.read8(true, h << 8 | l);

    result = ((a - n) - (carry ? 1 : 0));

    zero = (result & 0xFF) == 0;
    subtract = true;
    carry = result < 0;
    result &= 0xFF;
    halfCarry = ((result ^ n ^ a) & 0x10) == 0x10;

    a = result;
  }

  private void rlca() {
    carry = ((a >> 7) & 1) != 0;
    a <<= 1;

    if (carry) {
      a = a | (1);
    } else {
      a = a & ~(1);
    }

    a &= 0xFF;

    zero = false;
    subtract = false;
    halfCarry = false;
  }

  private void rlc_HL() {
    int a = memory.read8(true, h << 8 | l);

    carry = ((a >> 7) & 1) != 0;
    a <<= 1;

    if (carry) {
      a = a | (1);
    } else {
      a = a & ~(1);
    }

    a &= 0xFF;

    memory.write8(true, h << 8 | l, a);

    zero = a == 0;
    subtract = false;
    halfCarry = false;
  }

  private void rrca() {
    carry = ((a) & 1) != 0;
    a >>>= 1;

    if (carry) {
      a = a | (1 << 7);
    } else {
      a = a & ~(1 << 7);
    }

    a &= 0xFF;

    zero = false;
    subtract = false;
    halfCarry = false;
  }

  private void add_SP_n() {
    byte n = (byte) getN();

    char result = (char) (sp + n);

    zero = false;
    subtract = false;

    halfCarry = (result & 0xF) < (sp & 0xF);
    carry = (result & 0xFF) < (sp & 0xFF);

    sp = result;
  }

  private void rr_HL() {
    int a = memory.read8(true, h << 8 | l);

    boolean oldCarry = carry;

    carry = ((a) & 1) != 0;

    a >>>= 1;

    if (oldCarry) {
      a = a | (1 << 7);
    } else {
      a = a & ~(1 << 7);
    }

    a &= 0xFF;

    zero = a == 0;
    subtract = false;
    halfCarry = false;

    memory.write8(true, h << 8 | l, a);
  }

  // DOCUMENT MISTAKE CLEARS Z FLAG
  private void rla() {
    boolean oldCarry = carry;

    carry = ((a >> 7) & 1) != 0;
    a <<= 1;

    a &= 0xFF;

    if (oldCarry) {
      a = a | (1);
    } else {
      a = a & ~(1);
    }

    zero = false;
    subtract = false;
    halfCarry = false;
  }

  private void rl_HL() {
    int a = memory.read8(true, h << 8 | l);
    boolean oldCarry = carry;

    carry = ((a >> 7) & 1) != 0;
    a <<= 1;

    a &= 0xFF;

    if (oldCarry) {
      a = a | (1);
    } else {
      a = a & ~(1);
    }

    zero = a == 0;
    subtract = false;
    halfCarry = false;

    memory.write8(true, h << 8 | l, a);
  }

  private void rl_r() {
    int result = 0;
    boolean oldCarry = carry;

    switch (opcode & 0x7) {
      case 0x7:
        carry = ((a >> 7) & 1) != 0;
        a <<= 1;
        a &= 0xFF;

        if (oldCarry) {
          a = a | (1);
        } else {
          a = a & ~(1);
        }

        result = a;
        break;

      case 0x0:
        carry = ((b >> 7) & 1) != 0;
        b <<= 1;

        b &= 0xFF;
        if (oldCarry) {
          b = b | (1);
        } else {
          b = b & ~(1);
        }

        result = b;
        break;

      case 0x1:
        carry = ((c >> 7) & 1) != 0;
        c <<= 1;

        c &= 0xFF;
        if (oldCarry) {
          c = c | (1);
        } else {
          c = c & ~(1);
        }

        result = c;
        break;

      case 0x2:
        carry = ((d >> 7) & 1) != 0;
        d <<= 1;
        d &= 0xFF;
        if (oldCarry) {
          d = d | (1);
        } else {
          d = d & ~(1);
        }

        result = d;
        break;

      case 0x3:
        carry = ((e >> 7) & 1) != 0;
        e <<= 1;
        e &= 0xFF;
        if (oldCarry) {
          e = e | (1);
        } else {
          e = e & ~(1);
        }

        result = e;
        break;

      case 0x4:
        carry = ((h >> 7) & 1) != 0;
        h <<= 1;
        h &= 0xFF;
        if (oldCarry) {
          h = h | (1);
        } else {
          h = h & ~(1);
        }

        result = h;
        break;

      case 0x5:
        carry = ((l >> 7) & 1) != 0;
        l <<= 1;
        l &= 0xFF;
        if (oldCarry) {
          l = l | (1);
        } else {
          l = l & ~(1);
        }

        result = l;
        break;

      default:
        System.out.println("Unknown Operation " + Integer.toHexString(opcode));
        break;
    }

    zero = result == 0;
    subtract = false;
    halfCarry = false;
  }

  private void rlc_r() {
    int result = 0;

    switch (opcode & 0x7) {
      case 0x7:
        carry = ((a >> 7) & 1) != 0;
        a <<= 1;
        a &= 0xFF;
        if (carry) {
          a = a | (1);
        } else {
          a = a & ~(1);
        }

        result = a;
        break;

      case 0x0:
        carry = ((b >> 7) & 1) != 0;
        b <<= 1;
        b &= 0xFF;
        if (carry) {
          b = b | (1);
        } else {
          b = b & ~(1);
        }

        result = b;
        break;

      case 0x1:
        carry = ((c >> 7) & 1) != 0;
        c <<= 1;
        c &= 0xFF;
        if (carry) {
          c = c | (1);
        } else {
          c = c & ~(1);
        }

        result = c;
        break;

      case 0x2:
        carry = ((d >> 7) & 1) != 0;
        d <<= 1;
        d &= 0xFF;
        if (carry) {
          d = d | (1);
        } else {
          d = d & ~(1);
        }

        result = d;
        break;

      case 0x3:
        carry = ((e >> 7) & 1) != 0;
        e <<= 1;
        e &= 0xFF;
        if (carry) {
          e = e | (1);
        } else {
          e = e & ~(1);
        }

        result = e;
        break;

      case 0x4:
        carry = ((h >> 7) & 1) != 0;
        h <<= 1;
        h &= 0xFF;
        if (carry) {
          h = h | (1);
        } else {
          h = h & ~(1);
        }

        result = h;
        break;

      case 0x5:
        carry = ((l >> 7) & 1) != 0;
        l <<= 1;
        l &= 0xFF;
        if (carry) {
          l = l | (1);
        } else {
          l = l & ~(1);
        }

        result = l;
        break;

      default:
        System.out.println("Unknown Operation " + Integer.toHexString(opcode));
        break;
    }

    zero = result == 0;
    subtract = false;
    halfCarry = false;


  }

  private void rrc_r() {
    int result = 0;
    switch (opcode & 0x7) {
      case 0x7:
        carry = ((a) & 1) != 0;
        a >>= 1;

        if (carry) {
          a = a | (1 << 7);
        } else {
          a = a & ~(1 << 7);
        }

        result = a;
        break;

      case 0x0:
        carry = ((b) & 1) != 0;
        b >>= 1;

        if (carry) {
          b = b | (1 << 7);
        } else {
          b = b & ~(1 << 7);
        }

        result = b;
        break;

      case 0x1:
        carry = ((c) & 1) != 0;
        c >>= 1;

        if (carry) {
          c = c | (1 << 7);
        } else {
          c = c & ~(1 << 7);
        }

        result = c;
        break;

      case 0x2:
        carry = ((d) & 1) != 0;
        d >>= 1;

        if (carry) {
          d = d | (1 << 7);
        } else {
          d = d & ~(1 << 7);
        }

        result = d;
        break;

      case 0x3:
        carry = ((e) & 1) != 0;
        e >>= 1;

        if (carry) {
          e = e | (1 << 7);
        } else {
          e = e & ~(1 << 7);
        }

        result = e;
        break;

      case 0x4:
        carry = ((h) & 1) != 0;
        h >>= 1;

        if (carry) {
          h = h | (1 << 7);
        } else {
          h = h & ~(1 << 7);
        }

        result = h;
        break;

      case 0x5:
        carry = ((l) & 1) != 0;
        l >>= 1;

        if (carry) {
          l = l | (1 << 7);
        } else {
          l = l & ~(1 << 7);
        }

        result = l;
        break;

      default:
        System.out.println("Unknown Operation " + Integer.toHexString(opcode));
        break;
    }

    zero = result == 0;
    subtract = false;
    halfCarry = false;


  }

  private void rrc_HL() {
    int result = 0;
    int a = memory.read8(true, h << 8 | l);

    carry = ((a) & 1) != 0;
    a >>= 1;

    if (carry) {
      a = a | (1 << 7);
    } else {
      a = a & ~(1 << 7);
    }

    result = a;

    zero = result == 0;
    subtract = false;
    halfCarry = false;

    memory.write8(true, h << 8 | l, result);

  }

  private void sla_r() {
    int result = 0;
    switch (opcode & 0x7) {
      case 0x7:
        carry = ((a >> 7) & 1) != 0;
        a <<= 1;
        a &= 0xFF;
        result = a;
        break;

      case 0x0:
        carry = ((b >> 7) & 1) != 0;
        b <<= 1;
        b &= 0xFF;
        result = b;
        break;

      case 0x1:
        carry = ((c >> 7) & 1) != 0;
        c <<= 1;
        c &= 0xFF;
        result = c;
        break;

      case 0x2:
        carry = ((d >> 7) & 1) != 0;
        d <<= 1;
        d &= 0xFF;
        result = d;
        break;

      case 0x3:
        carry = ((e >> 7) & 1) != 0;
        e <<= 1;
        e &= 0xFF;
        result = e;
        break;

      case 0x4:
        carry = ((h >> 7) & 1) != 0;
        h <<= 1;
        h &= 0xFF;
        result = h;
        break;

      case 0x5:
        carry = ((l >> 7) & 1) != 0;
        l <<= 1;
        l &= 0xFF;
        result = l;
        break;

      default:
        System.out.println("Unknown Operation " + Integer.toHexString(opcode));
        break;
    }

    zero = result == 0;
    halfCarry = false;
    subtract = false;


  }

  private void sla_HL() {
    int result = 0;
    int a = memory.read8(true, h << 8 | l);

    carry = ((a >> 7) & 1) != 0;
    a <<= 1;
    a &= 0xFF;
    result = a;

    zero = result == 0;
    halfCarry = false;
    subtract = false;

    memory.write8(true, h << 8 | l, a);

  }

  private void sra_r() {
    int result = 0;

    switch (opcode & 0x7) {
      case 0x7:
        carry = ((a) & 1) != 0;
        a = (a >> 1) | (a & 0x80);
        result = a;
        break;

      case 0x0:
        carry = ((b) & 1) != 0;
        b = (b >> 1) | (b & 0x80);
        result = b;
        break;

      case 0x1:
        carry = ((c) & 1) != 0;
        c = (c >> 1) | (c & 0x80);
        result = c;
        break;

      case 0x2:
        carry = ((d) & 1) != 0;
        d = (d >> 1) | (d & 0x80);
        result = d;
        break;

      case 0x3:
        carry = ((e) & 1) != 0;
        e = (e >> 1) | (e & 0x80);
        result = e;
        break;

      case 0x4:
        carry = ((h) & 1) != 0;
        h = (h >> 1) | (h & 0x80);
        result = h;
        break;

      case 0x5:
        carry = ((l) & 1) != 0;
        l = (l >> 1) | (l & 0x80);
        result = l;
        break;

      default:
        System.out.println("Unknown Operation " + Integer.toHexString(opcode));
        break;
    }

    zero = result == 0;
    subtract = false;
    halfCarry = false;


  }

  private void sra_HL() {
    int result = 0;

    int a = memory.read8(true, h << 8 | l);

    carry = ((a) & 1) != 0;
    a = (a >> 1) | (a & 0x80);
    result = a;

    zero = result == 0;
    subtract = false;
    halfCarry = false;

    memory.write8(true, h << 8 | l, a);
  }

  private void bit_b_r() {
    boolean result = false;
    int bit = (opcode >> 3) & 0x7;

    switch (opcode & 0x7) {
      case 0x7:
        result = ((a >> bit) & 1) != 0;
        break;

      case 0x0:
        result = ((b >> bit) & 1) != 0;
        break;

      case 0x1:
        result = ((c >> bit) & 1) != 0;
        break;

      case 0x2:
        result = ((d >> bit) & 1) != 0;
        break;

      case 0x3:
        result = ((e >> bit) & 1) != 0;
        break;

      case 0x4:
        result = ((h >> bit) & 1) != 0;
        break;

      case 0x5:
        result = ((l >> bit) & 1) != 0;
        break;

      default:
        System.out.println("Unknown Operation " + Integer.toHexString(opcode));
        break;
    }

    zero = !result;
    subtract = false;
    halfCarry = true;

  }

  private void bit_b_HL() {
    boolean result = false;
    int bit = (opcode >> 3) & 0x7;

    int a = memory.read8(true, h << 8 | l);

    result = ((a >> bit) & 1) != 0;

    zero = !result;
    subtract = false;
    halfCarry = true;
  }

  private void res_b_r() {
    int bit = (opcode >> 3) & 0x7;

    switch (opcode & 0x7) {
      case 0x7:
        a &= ~(1 << bit);
        break;

      case 0x0:
        b &= ~(1 << bit);
        break;

      case 0x1:
        c &= ~(1 << bit);
        break;

      case 0x2:
        d &= ~(1 << bit);
        break;

      case 0x3:
        e &= ~(1 << bit);
        break;

      case 0x4:
        h &= ~(1 << bit);
        break;

      case 0x5:
        l &= ~(1 << bit);
        break;

      default:
        System.out.println("Unknown Operation " + Integer.toHexString(opcode));
        break;
    }
  }

  private void res_b_HL() {
    int bit = (opcode >> 3) & 0x7;
    int a = memory.read8(true, h << 8 | l);
    a &= ~(1 << bit);
    memory.write8(true, h << 8 | l, a);
  }

  private void set_b_r() {
    int bit = (opcode >> 3) & 0x7;

    switch (opcode & 0x7) {
      case 0x7:
        a |= (1 << bit);
        break;

      case 0x0:
        b |= (1 << bit);
        break;

      case 0x1:
        c |= (1 << bit);
        break;

      case 0x2:
        d |= (1 << bit);
        break;

      case 0x3:
        e |= (1 << bit);
        break;

      case 0x4:
        h |= (1 << bit);
        break;

      case 0x5:
        l |= (1 << bit);
        break;

      default:
        System.out.println("Unknown Operation " + Integer.toHexString(opcode));
        break;
    }
  }

  private void set_b_HL() {
    int bit = (opcode >> 3) & 0x7;

    int a = memory.read8(true, h << 8 | l);

    a |= (1 << bit);

    memory.write8(true, h << 8 | l, a);
  }

  private void ld_A_c() {
    a = memory.read8(true, 0xFF00 + c);
  }

  private void ld_c_A() {
    memory.write8(true, 0xFF00 + c, a);
  }

  private void ldhl_SP_n() {
    byte e = (byte) getN();

    zero = false;
    subtract = false;

    char result = (char) (sp + e);

    char check = (char) (sp ^ e ^ ((sp + e) & 0xFFFF));

    carry = ((check & 0x100) == 0x100);
    halfCarry = ((check & 0x10) == 0x10);

    h = (result >> 8) & 0xFF;
    l = result & 0xFF;
  }

  private void reti() {
    ime = true;
    pc = pop();
  }

  private void rst_n() {
    int n = ((opcode >> 3) & 0x07) * 8;
    push(pc);
    pc = (char) n;
  }

  private void push(char data) {
    sp -= 1;
    memory.write8(true, sp, (data >> 8) & 0xFF);
    sp -= 1;
    memory.write8(true, sp, data & 0xFF);
  }

  private char pop() {
    char data = 0;
    data = (char) memory.read8(true, sp);
    sp += 1;
    data = (char) (memory.read8(true, sp) << 8 | data);
    sp += 1;
    return data;
  }

  private int getFlags() {
    return ((zero ? 1 : 0) << 7) | ((subtract ? 1 : 0) << 6)
        | ((halfCarry ? 1 : 0) << 5) | ((carry ? 1 : 0) << 4);
  }

  private String printRegisters() {
    return ("OPCODE: " + (Integer.toHexString(opcode).toUpperCase()) + " AF: " + Integer
        .toHexString(a << 8 | getFlags()).toUpperCase() + " BC: " + Integer.toHexString(b << 8 | c)
        .toUpperCase() + " DE: " + Integer.toHexString(d << 8 | e)).toUpperCase() +
        " HL: " + Integer.toHexString(h << 8 | l).toUpperCase() + " SP: " + Integer.toHexString(sp)
        .toUpperCase() + " PC: " + Integer.toHexString(pc - 1).toUpperCase();
  }

  private int getN() {
    return memory.read8(true, pc++);
  }

  private char getNN() {
    int upper = (memory.read8(true, pc++));
    int lower = (memory.read8(true, pc++));
    return (char) ((upper) | ((lower) << 8));
  }

  public void requestInterrupt(int bit) {
    int interruptFlag = memory.read8(true, 0xFF0F);
    interruptFlag |= (1 << bit);
    memory.directWrite8(0xFF0F, interruptFlag);
  }

  public void checkInterrupts() {
    int interruptFlag = memory.read8(true, 0xFF0F);
    int interruptEnable = memory.read8(true, 0xFFFF);

    for (int i = 0; i < 5; ++i) {
      if ((((interruptFlag >> i) & 1) != 0)) {
        if ((((interruptEnable >> i) & 1) != 0)) {
          halted = false;
          if (ime) {
            executeInterrupt(i);
          }
        }
      }
    }
  }

  private void executeInterrupt(int bit) {
    int interruptFlag = memory.read8(true, 0xFF0F);
    ime = false;
    interruptFlag &= ~(1 << bit);
    memory.write8(true, 0xFF0F, interruptFlag);

    push(pc);

    switch (bit) {
      case 0:
        pc = 0x40;
        break;
      case 1:
        pc = 0x48;
        break;
      case 2:
        pc = 0x50;
        break;
      case 3:
        pc = 0x58;
        break;
      case 4:
        pc = 0x60;
        break;
    }
  }

  public void reset() {
    pc = 0x100;
    a = 0x01;
    b = 0x00;
    c = 0x13;
    d = 0x00;
    e = 0xd8;
    h = 0x01;
    l = 0x4D;
    sp = 0xFFFE;

    zero = true;
    subtract = false;
    halfCarry = true;
    carry = true;

    halted = false;
  }

  public void setMemory(Memory memory) {
    this.memory = memory;
  }

  public void setLCD(LCD lcd) {
  }
}