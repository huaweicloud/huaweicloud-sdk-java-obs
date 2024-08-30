package com.obs.services.internal.utils;

import com.obs.log.ILogger;
import com.obs.log.LoggerBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.zip.Checksum;

public class CRC64 implements Checksum, Serializable {
    private static final long serialVersionUID = 5684087791018577616L;
    private static final ILogger log = LoggerBuilder.getLogger(CRC64.class);

    private static final long POLY = (long) 0xc96c5795d7870f42L; // ECMA-182

    /* CRC64 calculation table. */
    private static final long[][] CRC64_TABLE;

    /* Current CRC value. */
    private long value;

    /**
     * Initialize with another CRC64's value.
     *
     * @param origin:
     *              original crc64
     */
    public CRC64(CRC64 origin) {
        this.value = origin.value;
    }

    /**
     * Initialize with a value of zero.
     */
    public CRC64() {
        this.value = 0;
    }

    /**
     * Initialize with a custom CRC value.
     *
     * @param value
     */
    public CRC64(long value) {
        this.value = value;
    }

    /**
     * Initialize by calculating the CRC of the given byte blocks.
     *
     * @param b
     *            block of bytes
     * @param len
     *            number of bytes to process
     */
    public CRC64(byte[] b, int len) {
        this.value = 0;
        update(b, len);
    }

    /**
     * Initialize by calculating the CRC of the given byte blocks.
     *
     * @param b
     *            block of bytes
     * @param off
     *            starting offset of the byte block
     * @param len
     *            number of bytes to process
     */
    public CRC64(byte[] b, int off, int len) {
        this.value = 0;
        update(b, off, len);
    }

    /**
     * Calculate the CRC64 of the given file's content.
     *
     * @param f
     * @return new {@link CRC64} instance initialized to the file's CRC value
     * @throws IOException
     *             in case the {@link FileInputStream#read(byte[])} method fails
     */
    public static CRC64 fromFile(File f) throws IOException {
        return fromInputStream(new FileInputStream(f));
    }

    /**
     * Calculate the CRC64 of the given {@link InputStream} until the end of the
     * stream has been reached.
     *
     * @param in
     *            the stream will be closed automatically
     * @return new {@link CRC64} instance initialized to the {@link InputStream}'s CRC value
     * @throws IOException
     *             in case the {@link InputStream#read(byte[])} method fails
     */
    public static CRC64 fromInputStream(InputStream in) throws IOException {
        try {
            CRC64 crc = new CRC64();
            byte[] b = new byte[4096];
            int l;

            while ((l = in.read(b)) != -1) {
                crc.update(b, l);
            }

            return crc;

        } finally {
            in.close();
        }
    }

    /**
     * Calculate the CRC64 of the given {@link InputStream} from offset to offset + sizeToReadTotal.
     *
     * @param in
     *            the stream will be closed automatically
     * @param offset
     *            the start offset in stream in at which the data is read.
     * @param sizeToReadTotal
     *            the maximum number of bytes to read.
     * @return new {@link CRC64} instance initialized to the {@link InputStream}'s CRC value
     * @throws IOException
     *             in case the {@link InputStream#read(byte[])} method fails
     */
    public static CRC64 fromInputStream(InputStream in, long offset, long sizeToReadTotal) throws IOException {
        try {
            long skippedSize = in.skip(offset);
            if (skippedSize != offset) {
                String errorInfo =
                        "Failed to skip the input stream to the specified offset:"
                                + offset
                                + ". actual skip size is "
                                + skippedSize;
                log.error(errorInfo);
                throw new IOException(errorInfo);
            }
            CRC64 crc = new CRC64();
            int bufferSize = 4096;
            byte[] b = new byte[bufferSize];
            int l;
            long sizeToRead = Long.min(sizeToReadTotal, bufferSize);
            while (sizeToRead > 0 && (l = in.read(b, 0, (int) sizeToRead)) != -1) {
                crc.update(b, l);
                sizeToReadTotal -= l;
                sizeToRead = Long.min(sizeToReadTotal, bufferSize);
            }
            return crc;

        } finally {
            in.close();
        }
    }

    /**
     * Get long representation of current CRC64 value.
     */
    public long getValue() {
        return this.value;
    }

    /**
     * Set long representation of current CRC64 value.
     */
    public void setValue(long value) {
        this.value = value;
    }

    /**
     * Update CRC64 with new byte block.
     */
    public void update(byte[] b, int len) {
        this.update(b, 0, len);
    }

    /**
     * Update CRC64 with new byte block.
     */
    public void update(byte[] b, int off, int len) {
        this.value = ~this.value;

        /* fast middle processing, 8 bytes (aligned!) per loop */

        int idx = off;
        while (len >= 8) {
            value =
                    CRC64_TABLE[7][(int) (value & 0xff ^ (b[idx] & 0xff))]
                            ^ CRC64_TABLE[6][(int) ((value >>> 8) & 0xff ^ (b[idx + 1] & 0xff))]
                            ^ CRC64_TABLE[5][(int) ((value >>> 16) & 0xff ^ (b[idx + 2] & 0xff))]
                            ^ CRC64_TABLE[4][(int) ((value >>> 24) & 0xff ^ (b[idx + 3] & 0xff))]
                            ^ CRC64_TABLE[3][(int) ((value >>> 32) & 0xff ^ (b[idx + 4] & 0xff))]
                            ^ CRC64_TABLE[2][(int) ((value >>> 40) & 0xff ^ (b[idx + 5] & 0xff))]
                            ^ CRC64_TABLE[1][(int) ((value >>> 48) & 0xff ^ (b[idx + 6] & 0xff))]
                            ^ CRC64_TABLE[0][(int) ((value >>> 56) ^ b[idx + 7] & 0xff)];
            idx += 8;
            len -= 8;
        }

        /* process remaining bytes (can't be larger than 8) */
        while (len > 0) {
            value = CRC64_TABLE[0][(int) ((this.value ^ b[idx]) & 0xff)] ^ (this.value >>> 8);
            idx++;
            len--;
        }

        this.value = ~this.value;
    }

    public void update(int b) {
        this.update(new byte[] {(byte) b}, 0, 1);
    }

    public void reset() {
        this.value = 0;
    }

    // dimension of GF(2) vectors (length of CRC)
    private static final int GF2_DIM = 64;

    private static long gf2MatrixTimes(long[] mat, long vec) {
        long sum = 0L;
        int idx = 0;
        while (vec != 0) {
            if ((vec & 1) == 1) {
                sum ^= mat[idx];
            }
            vec >>>= 1;
            idx++;
        }
        return sum;
    }

    private static void gf2MatrixSquare(long[] square, long[] mat) {
        for (int n = 0; n < GF2_DIM; n++) {
            square[n] = gf2MatrixTimes(mat, mat[n]);
        }
    }

    /*
     * calculate the CRC-64 of two sequential blocks and set it to this.value,
     * where this.value is the CRC-64 of the first block,
     * anotherCRC64.value is the CRC-64 of the second block, and len2 is the
     * length of the second block.
     */
    public void combineWithAnotherCRC64(CRC64 anotherCRC64, long len2) {
        this.value = combine(this.value, anotherCRC64.value, len2);
    }

    private static final long[] EVEN_SQUARE;
    private static final long[] ODD_SQUARE;

    static {
        /*
         * Nested tables as described by Mark Adler
         */
        CRC64_TABLE = new long[8][256];

        for (int n = 0; n < 256; n++) {
            long crc = n;
            for (int k = 0; k < 8; k++) {
                if ((crc & 1) == 1) {
                    crc = (crc >>> 1) ^ POLY;
                } else {
                    crc = (crc >>> 1);
                }
            }
            CRC64_TABLE[0][n] = crc;
        }

        /* generate nested CRC table for future slice-by-8 lookup */
        for (int n = 0; n < 256; n++) {
            long crc = CRC64_TABLE[0][n];
            for (int k = 1; k < 8; k++) {
                crc = CRC64_TABLE[0][(int) (crc & 0xff)] ^ (crc >>> 8);
                CRC64_TABLE[k][n] = crc;
            }
        }

        int n;
        long row;
        EVEN_SQUARE = new long[GF2_DIM]; // even-power-of-two zeros operator
        ODD_SQUARE = new long[GF2_DIM]; // odd-power-of-two zeros operator
        // put operator for one zero bit in odd
        ODD_SQUARE[0] = POLY; // CRC-64 polynomial
        row = 1;
        for (n = 1; n < GF2_DIM; n++) {
            ODD_SQUARE[n] = row;
            row <<= 1;
        }
        // put operator for two zero bits in even
        gf2MatrixSquare(EVEN_SQUARE, ODD_SQUARE);
        // put operator for four zero bits in odd
        gf2MatrixSquare(ODD_SQUARE, EVEN_SQUARE);
    }

    /*
     * Return the CRC-64 of two sequential blocks, where crc1 is the CRC-64 of
     * the first block, crc2 is the CRC-64 of the second block, and len2 is the
     * length of the second block.
     */
    public static long combine(long crc1, long crc2, long len2) {
        // degenerate case.
        if (len2 == 0) {
            return crc1;
        }

        long[] even = Arrays.copyOf(EVEN_SQUARE, EVEN_SQUARE.length);
        long[] odd = Arrays.copyOf(ODD_SQUARE, ODD_SQUARE.length);
        // apply len2 zeros to crc1 (first square will put the operator for one
        // zero byte, eight zero bits, in even)
        do {
            // apply zeros operator for this bit of len2
            gf2MatrixSquare(even, odd);
            if ((len2 & 1) == 1) {
                crc1 = gf2MatrixTimes(even, crc1);
            }
            len2 >>>= 1;

            // if no more bits set, then done
            if (len2 == 0) {
                break;
            }

            // another iteration of the loop with odd and even swapped
            gf2MatrixSquare(odd, even);
            if ((len2 & 1) == 1) {
                crc1 = gf2MatrixTimes(odd, crc1);
            }
            len2 >>>= 1;

            // if no more bits set, then done
        } while (len2 != 0);
        // return combined crc.
        crc1 ^= crc2;
        return crc1;
    }

    /**
     * Returns a string representation of the object. In general, the
     * {@code toString} method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * <p>
     * The {@code toString} method for class {@code Object}
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `{@code @}', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return Long.toUnsignedString(this.value);
    }

    public static String toString(long value) {
        return Long.toUnsignedString(value);
    }

    public static long fromString(String crcString) throws NumberFormatException {
        return Long.parseUnsignedLong(crcString);
    }

    @Override
    public int hashCode() {
        return Long.hashCode(this.value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (obj instanceof CRC64) {
            return ((CRC64) obj).value == this.value;
        }
        return false;
    }
}
