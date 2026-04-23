# data-knife

A streaming CLI tool for converting between data formats (JSON, CBOR, CSV), built with Scala 3, fs2, and fs2-data. Runs
on both JVM and Scala Native from a single shared source set.

While this tool may be useful for some, it is primarily intended as a demonstration of fs2-data's streaming parsing and
formatting capabilities. It is not optimized for maximum performance (though it should do okay while using only constant
memory) or feature completeness.

Claude Code has been used during development, with in-depth human reviews.

## Supported Conversions

| Command     | Description  |
|-------------|--------------|
| `cbor2json` | CBOR to JSON |
| `csv2cbor`  | CSV to CBOR  |
| `csv2json`  | CSV to JSON  |
| `json2cbor` | JSON to CBOR |

## Usage

```
data-knife <command> [options]
```

Input defaults to stdin and output to stdout, so pipes work naturally:

```bash
cat data.json | data-knife json2cbor > data.cbor
data-knife csv2json < input.csv > output.json
```

Use `--input`/`-i` and `--output`/`-o` for file paths:

```bash
data-knife cbor2json -i data.cbor -o data.json
```

Use `--url`/`-u` to fetch input from an HTTP URL:

```bash
data-knife json2cbor --url https://example.com/data.json -o data.cbor
```

### Format Options

| Flag                  | Applies to  | Description                                 |
|-----------------------|-------------|---------------------------------------------|
| `--csv-separator <c>` | CSV input   | Field separator (default: `,`)              |
| `--jq <query>`        | JSON input  | Apply a jq filter to JSON before conversion |
| `--diagnostic` / `-d` | CBOR output | Output CBOR in diagnostic text notation     |
| `--pretty` / `-p`     | JSON output | Pretty-print JSON                           |

### Examples

```bash
# Pretty-print CBOR as JSON
data-knife cbor2json -i data.cbor --pretty

# Convert semicolon-separated CSV to JSON
data-knife csv2json --csv-separator ';' < data.csv

# Convert CSV to CBOR diagnostic notation
data-knife csv2cbor -i data.csv --diagnostic
```

## Server Mode

The `server` subcommand starts an HTTP server that exposes all conversions as `POST /<input>/<output>` routes.

```bash
data-knife server [--host 0.0.0.0] [--port 9474]
```

The request body is the input data, and the response body is the converted output. Format options are passed as query parameters:

| Parameter     | Description                                 |
|---------------|---------------------------------------------|
| `separator`   | CSV field separator (default: `,`)          |
| `jq`          | jq filter to apply to JSON input            |
| `pretty`      | Set to `true` to pretty-print JSON output   |
| `diagnostic`  | Set to `true` for CBOR diagnostic notation  |

### Examples

```bash
# Start the server
data-knife server

# Convert JSON to CBOR diagnostic notation
curl -X POST --data-binary @data.json 'http://localhost:9474/json/cbor?diagnostic=true'

# Convert CSV to pretty-printed JSON
curl -X POST --data-binary @data.csv 'http://localhost:9474/csv/json?pretty=true'

# Convert CSV with custom separator to CBOR
curl -X POST --data-binary @data.csv 'http://localhost:9474/csv/cbor?separator=;'
```

## Building and Running

Requires **sbt** and **Scala 3**.

### JVM

```bash
sbt "run csv2json -i data.csv"
```

### Scala Native

Requires [Scala Native prerequisites](https://scala-native.org/en/stable/user/setup.html) (LLVM/Clang) plus the
[s2n-tls](https://github.com/aws/s2n-tls) library, used by http4s-ember for HTTPS (`brew install s2n` on macOS).

```bash
sbt rootNative/nativeLink
./.native/target/scala-3.8.3/native/dataknife.Main csv2json -i data.csv
```