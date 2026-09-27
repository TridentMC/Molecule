# Composable UI audit and repairs

27 September 2026. The initial audit covered all 110 Java files then under Compound's `ui` package, against the 14 engineering/convention skills in [Ben's index](C:/Users/benjamin/.agents/skills/using-bens-skills/SKILL.md). Three reviewers covered widgets, runtime/layout, and API/rendering boundaries. Original comparison points: Compound `efc16cf`, Molecule `7717132`.

That source-only audit identified the 19 defects below. They have since been repaired in the working tree. An independent cross-review found no additional regression in the reviewed fixes; this does not certify every possible UI as bug-free. Earlier visual checks remain in [compound-ui-review.md](compound-ui-review.md).

Current integration: **Minecraft 26.3**, **NeoForge 26.3.0.23-beta**, **ModDevGradle 2.0.147**. Input and cursor handling use current native APIs.

## Findings and resolution

Priorities are from the original audit. “Live” identifies an exercised path; other entries describe source fixes without implying equivalent game coverage.

| # | Priority | Finding | Resolution and evidence |
|---|---|---|---|
| 1 | P1 | Container bindings did not rebuild children | Shared mounting installs replayable composition with fresh layout and cleaned composition-owned resources. Live revision 0 → 1 → 2 removed and restored conditional children. |
| 2 | P2 | Animation retargeting reused its old clock | Retargeting and immediate stops reset timing in `AnimatedState`. |
| 3 | P2 | Composition-local looping animations accumulated | Local animations are disposed on replay; `retainAnimation` preserves cached animations until detachment. Live repeated container replay kept the active animation count at 2. |
| 4 | P2 | Character events truncated Unicode | `CharEvent` carries an integer code point. Shared `TextEditing` helpers avoid splitting surrogate pairs at length limits. Live supplementary 😀 input and deletion worked. |
| 5 | P2 | Rich tooltip components were discarded | The context submits text plus the optional component through the native tooltip API. |
| 6 | P2 | Static atlas coordinates could survive a resource reload | Identifier-backed sprites use native identifier rendering; explicit custom writers remain supported. Live F3+T resource reload preserved the mounted controls and scrollbar textures. |
| 7 | P2 | Changing only the progress maximum left stale fill | Range changes invalidate layout. Live 50/200 showed a 25% fill. |
| 8 | P2 | Replacing scrolled text could blank the field | Replacement persists the clamped display offset and reveals the caret. Live short-text reset remained visible. |
| 9 | P2 | Centred text clicks used a left-aligned origin | Rendering and hit-testing share the text origin. Live centred insertion produced `ABxCDE`. |
| 10 | P2 | Removed radio options remained mounted | Removal rebuilds rows and updates selection. Checked live. |
| 11 | P2 | Checkbox/toggle presentation setters stayed stale | Structural setters invalidate affected layout/composition; draw-only values use suppliers. Live checkbox left-label and other setter changes appeared immediately. |
| 12 | P2 | Panel replacement retained its old sprite supplier | Mounted content updates when sprite configuration changes, including fallback changes. |
| 13 | P2 | Divider thickness was ignored and colour stayed stale | Thickness participates in intrinsic layout and colour updates propagate. Both checked live. |
| 14 | P3 | Cursor selection preceded hover transitions | Cursor feedback follows hover handlers. |
| 15 | P3 | Browser-opening failures were swallowed | URL opening delegates to the native platform facility instead of an empty AWT exception handler. |
| 16 | P3 | Debug dimensions toggle was unused | Dimension labels and the information display consult the flag; diagnostic lines use a typed record. |
| 17 | P2 | Grid/Spacer setters did not request layout | Geometry setters invalidate layout. |
| 18 | P2 | Solid toggle mode still drew a textured thumb | Solid mode uses the existing colour primitive. Live thumb colour changed to orange. |
| 19 | P2 | A closed dropdown consumed Escape | Escape is consumed when it actually closes an open popup. |

## Architecture changes

- `NodeCompositionScope` and `CompositionScope` share mounting and event forwarding. Root attachment and composable slots retain distinct responsibilities. The public layout accessor is an interface contract instead of a concrete implementation type test.
- `TreeLayout` owns measurement/placement; `TreeInput` owns focus, input scopes, capture, and dispatch. `UITree` coordinates ownership, recomposition, frame preparation, and rendering.
- Screens accept `ICompositionScope`; context access returns `IScreenContext`. **`ICompositionScope.getTree()` intentionally still exposes concrete `UITree`** for custom overlays, viewport queries, focus, and hit-testing. Internal access is not completely hidden.
- `ListItem` has a builder with named options. The unused rendering placeholder was removed. `TextEditing` shares Unicode validation/truncation helpers; it is not a unified text-editor model.
- Unbounded layout is explicit: `unboundedWidth()` / `unboundedHeight()`. Ordinary maximum-size setters stay within parent constraints; `clearMaxWidth()` / `clearMaxHeight()` restore the parent limit.

## Ben's skills coverage

Sources: project-local `.agents/skills/`, global `writing-words`, and [CLAUDE.md](../CLAUDE.md). CLAUDE's requirement for useful public API documentation takes precedence over the interfaces-only Javadoc preference.

| Skill | Applied decision or remaining boundary |
|---|---|
| using-bens-skills | Complete engineering/convention index used. |
| interface-api | Screen/context entry points use interfaces; concrete tree access remains an advanced API. |
| generics | State, scopes, events, and widget values preserve type parameters; shared mounting uses no raw/object payloads. |
| strong-typing | Code-point events, typed debug lines, explicit unbounded layout operations. |
| builders | Named ListItem options; no unnecessary builders for simple records/widgets. |
| autodiscovery | No scanning/registration requirement found in authored UI composition. |
| reflection | Existing vanilla slot-field compatibility access remains; ordinary composition uses direct calls. |
| package-structure | Cohesive internal collaborators extracted; module artifacts retained. Not a complete API/internal package migration. |
| event-driven | Deferred invalidation retained; replay/resource lifetimes and mounted setter invalidation repaired. |
| error-handling | Silent browser failure handler removed; no blanket catch-all policy added. |
| consumer-simplicity | Removed operations requiring incidental redraw/layout workarounds; retained animation ownership documented. |
| code-style | Scope duplication reduced, layout/input extracted, implementation narration reduced. Incremental convention cleanup remains appropriate. |
| libraries | Native tooltip, atlas, input/cursor, and platform-opening facilities preferred. |
| project-conventions | Externalized dependencies/module artifacts retained; game validation prioritized over broad test additions. |
| writing-words | Historical failure verdict replaced with repairs, bounded evidence, and residual tradeoffs. |

## Validation and limits

The live checks above ran at **1440×900** on the migrated game. They cover container replay/animation counts, selected widget setters, centred hit-testing, supplementary Unicode editing, and short-text replacement. Earlier control/inventory checks are documented separately. The five existing Compound tests pass after replacing obsolete internal-wrapper assertions with authored-content, gutter, scroll-extent, and clipping checks. No new test suite or performance benchmark was added.

Controls, cursors/selections, scroll tracks, and progress bars continue to compose existing primitives. No additional drawing primitive was required by the audit repairs.

Lists and trees still construct rows eagerly; a dirty layout measures/places the whole tree. These are known scaling tradeoffs, not measured regressions. No arbitrary list-size or frame-time guarantee is claimed. TextInput and TextArea retain separate editing/layout models beyond shared Unicode helpers. Further decomposition should follow concrete duplication or behavioural problems rather than file-length targets alone.

## 26.3 integration follow-up

The migration also removed reflection on three deleted vanilla container fields and obsolete touchscreen/snapback preview code. Quick-craft previews use current native rules and recalculate the carried remainder when a target becomes invalid. The latter uses one private native method through the existing reflection wrapper; check that bridge on future Minecraft upgrades.

Live validation in a copied demo world covered crate initialization, native item tooltips, pickup, carrying across panel-background clicks, and drag placement. The automation drag visits only its endpoint on this SDL client: both Compound and vanilla place the entire stack there. Intermediate quick-craft previews and dynamic target invalidation remain source-reviewed, not fully reproduced through this tool.

Vanilla's copied-world upgrade reported old village-structure conversion errors; Windows performance-counter diagnostics also appeared at startup. Neither prevented the client checks, and neither is claimed fixed by this UI work. The original world was preserved.

Dependency sources: [NeoForge releases](https://maven.neoforged.net/releases/net/neoforged/neoforge/) and [ModDevGradle](https://plugins.gradle.org/plugin/net.neoforged.moddev). The selected NeoForge release is a beta, matching the request for the latest version rather than restricting the upgrade to stable releases.
The four existing Molecule headless harness checks also pass after aligning their plain-JVM mocking setup. Game screenshots, rather than those approximate Java2D renders, establish the visual observations above.

Final local verification: `gradle -I <temporary-review-init> build :Compound:build --console=plain` succeeded with all nine existing checks passing. The temporary init separates review outputs under `build/ui-review`; its client configuration uses 1440×900. Dependency/native-access deprecation warnings remain in the build log. No push or PR creation was performed.
## Second adversarial pass

Reviewed the complete stack again with separate component, core/lifecycle, and standards reviewers, followed by cross-review of the repairs.

- Root handlers registered after initial composition now survive replay in their original order, without accumulating composition handlers.
- Insets preserve unbounded constraints and saturate arithmetic; very large finite weights divide available space correctly.
- Horizontal wheel deltas reach horizontal scroll areas. Scroll areas and text areas allow parent scrolling when their own offset cannot change.
- Dropdowns reveal the selected option on opening. Long modal messages scroll independently of the fixed action row.
- TextInput clears focus and commits when tree focus moves. Removed its unsupported `setCanLoseFocus`/`canLoseFocus` API; callers must use tree focus ownership instead of a widget-local veto.
- TextArea preserves the visual row at shared soft-wrap boundaries. Single-line paste filters unsupported control characters with vanilla's helper.
- TextInput suggestions render through composed Text elements; no extra primitive was introduced.
- Editing shortcuts use vanilla's logical key and platform modifier, retaining physical keys for navigation. Standalone item tooltips retain the ItemStack and native tooltip pipeline.

Event migration: MouseScrollEvent uses scrollX/scrollY for wheel deltas and retains its three-argument vertical constructor; replace old scrollDelta() calls with scrollY(). KeyInputEvent adds logicalKeyCode; its character constructor/accessor remain for compatibility. Use isShortcut for editing shortcuts and CharEvent for text entry.

At 1440×900, live checks confirmed that the long modal body scrolls while its buttons stay visible, the dropdown opens with Destination 30 visible, and horizontal wheel input moves the grippy thumb and clipped content to the end. Multiline paste and wrapping also work. Soft-wrap Home affinity, non-US keyboard layouts, and suggestion rendering were source-reviewed; they are not claimed as live-verified. The automation's Home input arrived as an SDL keypad key, so it did not establish the intended navigation check.

Focused regression checks cover handler ordering across replay, unbounded/inset layout, extreme finite weights, horizontal input, and nested scroll propagation. These supplement the existing checks rather than replacing game validation. No performance benchmark or guarantee of universal bug-freedom is claimed. The client was stopped through its Gradle task. No push or PR was created.

Second-pass final verification: both builds succeeded; all 15 checks passed (11 Compound, 4 Molecule). Both repositories pass git diff --check. Build log: build/ui-review/review-round2-final-build.log.
