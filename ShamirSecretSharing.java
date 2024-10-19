import org.json.JSONArray;
import org.json.JSONObject;
import java.io.FileReader;
import java.io.IOException;

public class ShamirSecretSharing {

    public static void main(String[] args) throws IOException {
        // File path for the JSON file containing both test cases
        String filePath = "testcases.json";  // Update with your actual file path

        // Step 1: Read the test case from the JSON file
        JSONObject input = readJsonFromFile(filePath);
        JSONArray testCases = input.getJSONArray("testCases");

        // Loop through each test case
        for (int t = 0; t < testCases.length(); t++) {
            JSONObject testCase = testCases.getJSONObject(t);

            // Step 2: Parse n and k values
            int n = testCase.getJSONObject("keys").getInt("n");
            int k = testCase.getJSONObject("keys").getInt("k");

            // Prepare arrays for x and y values
            double[] xValues = new double[n];
            double[] yValues = new double[n];

            // Step 3: Decode the y values
            for (int i = 1; i <= n; i++) {
                JSONObject point = testCase.getJSONObject(String.valueOf(i));
                int base = Integer.parseInt(point.getString("base"));
                String value = point.getString("value");

                // Fill x and y arrays
                xValues[i - 1] = i;  // x values are the indices (1 to n)
                yValues[i - 1] = decodeValue(value, base);  // Decode y based on base
            }

            // Step 4: Solve for the coefficients (including c)
            double[] coefficients = solvePolynomial(xValues, yValues, k);

            // Step 5: Output the constant term (c) which is the first coefficient
            System.out.println("Test Case " + (t + 1) + ": Secret (Constant term c): " + (int) coefficients[0]);
        }
    }

    // Function to decode the value based on its base
    public static long decodeValue(String value, int base) {
        return Long.parseLong(value, base);
    }

    // Function to solve the polynomial using the matrix method
    public static double[] solvePolynomial(double[] x, double[] y, int k) {
        double[][] matrix = new double[k][k + 1];  // Augmented matrix for k points

        // Construct the augmented matrix
        for (int i = 0; i < k; i++) {
            double xi = x[i];
            for (int j = 0; j < k; j++) {
                matrix[i][j] = Math.pow(xi, j);  // Fill in powers of x
            }
            matrix[i][k] = y[i];  // The right-hand side (y values)
        }

        // Solve the system using Gaussian elimination
        return gaussianElimination(matrix);
    }

    // Gaussian elimination to solve the augmented matrix
    public static double[] gaussianElimination(double[][] matrix) {
        int n = matrix.length;

        // Forward elimination
        for (int i = 0; i < n; i++) {
            for (int k = i; k < n; k++) {
                if (i == k) {
                    double factor = matrix[i][i];
                    for (int j = 0; j < n + 1; j++) {
                        matrix[i][j] /= factor;
                    }
                } else {
                    double factor = matrix[k][i];
                    for (int j = 0; j < n + 1; j++) {
                        matrix[k][j] -= factor * matrix[i][j];
                    }
                }
            }
        }

        // Back substitution
        double[] result = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            result[i] = matrix[i][n];
            for (int j = i + 1; j < n; j++) {
                result[i] -= matrix[i][j] * result[j];
            }
        }

        return result;
    }

    // Utility function to read JSON from a file
    public static JSONObject readJsonFromFile(String filePath) throws IOException {
        FileReader reader = new FileReader(filePath);
        StringBuilder jsonBuilder = new StringBuilder();
        int ch;

        while ((ch = reader.read()) != -1) {
            jsonBuilder.append((char) ch);
        }

        reader.close();
        return new JSONObject(jsonBuilder.toString());
    }
}
