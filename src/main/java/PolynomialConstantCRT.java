import java.math.BigInteger;
import java.nio.file.*;
import java.util.*;
import org.json.JSONObject;

public class PolynomialConstantCRT {


    private static final long[] MODS = {
            1000000007L, 1000000009L, 1000000033L,
            1000000087L, 1000000093L, 1000000097L
    };

    private static BigInteger modInverse(BigInteger a, BigInteger m) {
        return a.modInverse(m);
    }

    private static BigInteger crt(long[] residues, long[] mods) {
        BigInteger prod = BigInteger.ONE;
        for (long mod : mods) prod = prod.multiply(BigInteger.valueOf(mod));

        BigInteger result = BigInteger.ZERO;
        for (int i = 0; i < mods.length; i++) {
            BigInteger m = BigInteger.valueOf(mods[i]);
            BigInteger Mi = prod.divide(m);
            BigInteger inv = modInverse(Mi.mod(m), m);
            result = result.add(BigInteger.valueOf(residues[i]).multiply(Mi).multiply(inv));
        }
        return result.mod(prod);
    }

    public static void main(String[] args) throws Exception {
        // Load JSON input
        String jsonData = new String(Files.readAllBytes(Paths.get("input.json")));
        JSONObject obj = new JSONObject(jsonData);

        int n = obj.getJSONObject("keys").getInt("n");

        long[] residues = new long[MODS.length];

        Arrays.fill(residues, 1L);

        for (String key : obj.keySet()) {
            if (!key.equals("keys")) {
                JSONObject root = obj.getJSONObject(key);
                int base = Integer.parseInt(root.getString("base"));
                String value = root.getString("value");

                BigInteger r = new BigInteger(value, base);

                for (int i = 0; i < MODS.length; i++) {
                    residues[i] = (residues[i] * r.mod(BigInteger.valueOf(MODS[i])).longValue()) % MODS[i];
                }
            }
        }

        BigInteger product = crt(residues, MODS);

        if (n % 2 != 0) {
            product = product.negate();
        }

        System.out.println("Constant term c = " + product);
    }
}
