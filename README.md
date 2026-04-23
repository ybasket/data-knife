# data-knife

A streaming CLI tool for converting between data formats (JSON, CBOR, CSV), built with Scala 3, fs2, and fs2-data. Runs
on both JVM and Scala Native.

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

On JVM, `--url`/`-u` fetches input from an HTTP URL:

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

## Building and Running

Requires **sbt** and **Scala 3**.

### JVM

```bash
sbt jvm/run -- csv2json -i data.csv
```

### Scala Native

Requires [Scala Native prerequisites](https://scala-native.org/en/stable/user/setup.html) (LLVM/Clang).

```bash
sbt native/nativeLink
./native/target/scala-3.8.3/native/dataknife.Main csv2json -i data.csv
```