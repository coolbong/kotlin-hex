# Improvement Tasks Checklist

Note: Each task is actionable and ordered from foundational architecture decisions to code quality, testing, documentation, tooling, and future enhancements.

1. [ ] Decide and document the mutability model for Hex
   - [ ] Either (A) make Hex truly immutable (remove mutating setters), or (B) embrace controlled mutability (keep setters) and adjust docs accordingly
   - [ ] Choose one approach, create a tracking issue, and document the decision in README and KDoc
   - [ ] If (A): provide copy-with APIs (e.g., with(index, value)) and deprecate/remove operator set; if (B): add clear side-effects and thread-safety notes

2. [ ] Align README with actual behavior
   - [ ] Fix "불변(Immutable)" claim if mutability remains
   - [ ] Add examples for mid/left/right, padding, bitwise ops, and hashing
   - [ ] Document index/length semantics (units = bytes, hex string pairs = 1 byte)

3. [ ] Refactor package/module structure for separation of concerns
   - [ ] Split responsibilities into: core (storage, equality, compare), codecs (ASCII/UTF-8/hex parsing/format), hashing, bitwise, padding
   - [ ] Keep the public API surface stable via re-exports or companion forwarding

4. [ ] Optimize compareTo implementation
   - [ ] Replace toString()-based comparison with lexicographical byte-array comparison to avoid allocations
   - [ ] Add unit tests for ordering semantics (e.g., 00 < 01 < FF00 < FF01)

5. [ ] Standardize Charset usage
   - [ ] Replace Charset.forName("ASCII") with StandardCharsets.US_ASCII
   - [ ] Consider adding UTF-8 helpers if needed; be explicit about encoding in all APIs

6. [ ] Improve factory APIs for clarity and safety
   - [ ] Add explicit from(bytes: ByteArray): Hex that copies defensively
   - [ ] Validate from(hexString) to allow optional whitespace consistently and consider 0x prefix handling as an opt-in
   - [ ] Add overloads for from(hexString, ignoreWhitespace: Boolean = true)

7. [ ] Consolidate and standardize error messages and validations
   - [ ] Make require() messages consistent (language, phrasing)
   - [ ] Ensure all index/length validations use the same boundary rules and wording
   - [ ] Add tests covering negative indices, overshoot, and zero-length cases

8. [ ] Clarify size vs. displayed hex length in API docs
   - [ ] KDoc: size is bytes; toString length is bytes * 2
   - [ ] Add examples demonstrating size vs. string length

9. [ ] Review and document performance characteristics
   - [ ] Document that toString() allocates; avoid in hot paths
   - [ ] Benchmark bitwise operations and padding for large arrays
   - [ ] Consider lazy hex caching (with invalidation) if justified

10. [ ] Ensure defensive copying everywhere appropriate
    - [ ] Verify toBytes() returns a copy (already done)
    - [ ] If setters remain, note that toBytes() remains safe but internal buffer is mutable

11. [ ] Add equals/hashCode/security notes
    - [ ] Confirm equals/hashCode use content-based comparison (already true)
    - [ ] Consider constant-time equals for security-sensitive use cases (provide equalsConstantTime(other: Hex))

12. [ ] Extend bitwise operations API ergonomics
    - [ ] Support operating on different sizes by defining semantics (e.g., pad shorter with zeros to left/right) via explicit methods or disallow with clear rationale
    - [ ] Add tests for mismatched sizes behavior (keep current strict behavior unless changed)

13. [ ] Improve padding API
    - [ ] Clarify semantics when requested length <= current size (currently returns self)
    - [ ] Add convenience methods: lpadToMultipleOf(blockSize, padByte), rpadToMultipleOf(blockSize, padByte)

14. [ ] Add safe subarray helpers
    - [ ] Provide safeSlice(start, length) that clamps instead of throwing, with clear naming
    - [ ] Keep current strict methods, document differences

15. [ ] Add additional codecs (optional)
    - [ ] Base64 encode/decode helpers (toBase64, fromBase64)
    - [ ] URL-safe Base64 if needed

16. [ ] API consistency and naming
    - [ ] Consider renaming u1/u2/un to readByte/readShort/readN or slice1/slice2/sliceN for clarity
    - [ ] Keep old names with @Deprecated and migration hints

17. [ ] Provide bulk operations
    - [ ] map/zip-like operations over bytes (e.g., map(transform: (Byte) -> Byte): Hex)
    - [ ] concat(vararg hex: Hex): Hex utility

18. [ ] Expand test coverage
    - [ ] Property-based tests for from/to symmetry (hex -> bytes -> hex)
    - [ ] Edge case tests for empty, single-byte, large inputs
    - [ ] Tests for error messages content (where stable)

19. [ ] Static analysis and code style
    - [ ] Add ktlint/spotless for formatting
    - [ ] Add detekt for static analysis and define a baseline
    - [ ] Integrate into Maven build (fail on violations)

20. [ ] Continuous Integration
    - [ ] Add GitHub Actions workflow for build + tests on push/PR
    - [ ] Cache Maven dependencies for faster builds

21. [ ] Publishing and versioning
    - [ ] Add Maven coordinates and configure publishing to a repository (e.g., OSSRH)
    - [ ] Adopt Semantic Versioning and CHANGELOG.md (Keep a Changelog format)
    - [ ] Add LICENSE file (e.g., Apache-2.0 or MIT) if missing

22. [ ] Improve README
    - [ ] English section alongside Korean, or choose one consistently
    - [ ] Add badges (CI, Maven Central, license)
    - [ ] Add quick-start, API overview, and common recipes

23. [ ] KDoc coverage
    - [ ] Add KDoc for all public methods and parameters, including edge cases and exceptions

24. [ ] Example module or samples
    - [ ] Provide a small sample demonstrating typical operations and pitfalls

25. [ ] Performance microbenchmarks (optional)
    - [ ] Set up JMH or Kotlinx-benchmark for critical paths (toString, compareTo, bitwise)

26. [ ] Backward compatibility plan
    - [ ] If renaming/removing methods, add deprecations with ReplaceWith and a migration guide

27. [ ] Thread-safety documentation
    - [ ] Clearly specify whether Hex instances are thread-safe under current mutability model

28. [ ] Error handling strategy
    - [ ] Decide between IllegalArgumentException vs. custom exceptions for parsing/validation; document rationale

29. [ ] API surface audit
    - [ ] Review which methods should be @JvmStatic/@JvmOverloads for better Java interop
    - [ ] Ensure overloads don't cause ambiguity from Java

30. [ ] Build configuration updates
    - [ ] Align README requirements with pom.xml (Kotlin/JDK versions)
    - [ ] Consider toolchain configuration for consistent JDK
