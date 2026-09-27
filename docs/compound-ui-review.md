# Compound composable UI review

Updated 2026-09-27 for the current Compound and Molecule working trees. The initial review found substantial correctness, input, layout, and appearance defects. Production fixes and a playable widget gallery have since been added, and the interactions below have been exercised in Minecraft. This is a record of implementation and verification, not a claim that every widget is bug-free or that performance has been benchmarked.

## Review history

The first game session on 2026-09-26 showed a blank configured numeric field, non-scrolling controls, a missing scrollbar thumb, an offscreen dropdown, and unreliable popup dismissal. Source inspection also found slider integer division, cursor-boundary exceptions, stale mounted widgets, missing focus navigation, and lifecycle/subscription defects. These findings drove the fixes described below.

The original crate and vanilla creative-inventory comparison screenshots are in `run/screenshots/2026-09-26_23.49.20.png` and `run/screenshots/2026-09-26_23.49.53.png`. They document the initial state. The separate crate asset defect was corrected with a native barrel asset and verified in the game.

## Live game evidence

Validation used Minecraft 26.1.2 with NeoForge 26.1.2.68-beta and the Compound widget gallery. Vanilla source comparisons used `build/moddev/artifacts/minecraft-patched-26.1.2.68-beta-sources.jar`, including AbstractWidget, AbstractButton, EditBox, MultilineTextField, CycleButton, and TabButton.

| Interaction | Observed result |
| --- | --- |
| Scroll area | Wheel scrolling and thumb dragging work. |
| List with 100 rows | Uses the available height. Thumb dragging reaches row 100; wheel scrolling works. |
| Dropdown with 40 options | Near the bottom edge, the popup opens above its anchor. Scrolling reaches and selects option 40. |
| Modal | Padding is correct; input is isolated. Escape dismisses it; Tab and Enter reach and activate its controls. |
| Tooltip | Native tooltip appears after its delay and wraps long text. |
| Context menu | Menu actions execute. |
| Slider | Midpoint selection gives an intermediate value. Dragging beyond the track continues and clamps at the bounds. |
| Accordion | Disclosure opens and closes its content. |
| Text editing | Selection, cut, and paste work. Configured numeric text is visible. Multiline wrapping and the empty-field hint were checked after their fixes. |
| NumberInput | Entering 150 and pressing Enter normalizes the displayed value to the configured maximum of 100. |
| More controls | Radio selection is exclusive; the toggle changes state; progress advances from 65 to 70; the animated quarter-width sweep moves. |
| Window resize | At 1440 x 900 and after maximization, the selected tab, tree selection, and entered text survive. |
| Container inventory | In the final build, a four-item player stack was picked up and placed into crate slot 1, then hovered to display its native Light Blue Wool tooltip. After maximization, container layout and hover coordinates remained correct. |
| Vanilla geometry | The chest-style container uses 176 x 168 geometry. Checkbox size is 17 pixels with a 4-pixel label gap. Slider and bordered single-line input text start at y + 6 in 20-pixel controls. |

ListView focus was confirmed in input traces. The automation's arrow/End key events arrived as GLFW key code -1, so those events could not exercise the implemented navigation handlers. Live arrow/Home/End coverage for list/tree navigation remains limited by this input path. No claim of successful keyboard navigation follows from the focus trace alone.

Final visual evidence:

- [Gallery baselines and insets](D:/Modding/Minecraft/Molecule/run/screenshots/2026-09-27_01.01.38.png).
- [Native inventory tooltip](D:/Modding/Minecraft/Molecule/run/screenshots/2026-09-27_01.02.28.png).

## Framework repairs

Paths in this section are beneath `Compound/src/main/java/com/tridevmc/compound/ui/`.

- `layout/DeferredScope.java` deduplicates state subscriptions, applies deferred properties before invalidating layout, and removes observers with their owning node. Container and composable scopes now supply their node to deferred layout.
- `tree/UITree.java` queues and deduplicates structural invalidations. Dirty layout is measured and placed once after the queued work is flushed, using the actual viewport constraints. Handler ownership survives recomposition; detached nodes and screen reinitialization are cleaned up.
- Input routing includes pointer capture, focusability, Tab traversal, popup/modal input ownership, and focus restoration. Focus narration uses Minecraft's narrator settings. This supplies a baseline; complete screen-reader parity still needs broader validation.
- Row and Column consume layout weights to distribute remaining space. Tabs use remaining height for content beneath their headers.
- Animation ownership survives ordinary recomposition. Retained text inputs and toggles clear disposed animation references on detach so remounting can create active animations.
- Layout debug rendering moved out of UITree into `debug/LayoutDebugRenderer.java`, reducing unrelated rendering code in the tree controller.

## Widget repairs and coverage

| Area | Current implementation |
| --- | --- |
| Button | Primary-button handling, keyboard activation, focus state, live sprite suppliers, and configurable vanilla sprite sets. Icon content composes through the existing slot. |
| Slider | Floating-point pointer normalization, narrow-track guard, range-relative steps, valid initial value, drag capture, and a centred label using vanilla's one-pixel baseline adjustment. Hover visuals use suppliers. |
| TextInput / NumberInput | Bounded Unicode cursor movement and deletion; pre-layout scrolling no longer hides initial text. NumberInput enforces integer mode and finite values, permits intermediate range edits, and normalizes on Enter or focus loss. |
| TextArea | Visible-line scrolling and clipping, wrapped hints, native font-based soft wrapping with original-string offsets, selection/cut/paste, word and page navigation, and cursor preservation during reflow. |
| Label / Text | Mounted setters read current suppliers. `Label.setWrap(true)` uses cached, styled line splitting in the existing Text primitive. |
| ScrollArea | Stable scrollbar structure with geometry derived after layout; working wheel and thumb interaction. |
| Dropdown | Bounded scrolling options, screen-edge-aware popup placement, dismissal, and input ownership. Options beyond the initial viewport remain reachable. |
| ListView | Mounted collection changes invalidate structure. A typed row-content callback supports composed icons, text, and actions. Dark background, selection outline, retained scrolling, keyboard navigation, selection reveal, and narration are implemented. Live navigation-key coverage is limited as described above. |
| TreeView | Expansion and collection changes update the displayed tree. Selection survives resize. Keyboard navigation and narration are implemented; live navigation-key coverage is limited as described above. |
| Tabs | Native tab sprites distinguish selection and hover. Headers and enabled/selected state update after mutation; content uses available remaining height. |
| Modal / ContextMenu | Composed surfaces, corrected sizing/padding, action controls, and shared overlay input handling replace the initial paint-only behaviour. |
| Tooltip | A usable content anchor, elapsed hover delay, and native rendering replace the zero-size, movement-only implementation. |
| Accordion | New composable disclosure control with arbitrary content. |
| CycleButton | New typed option-cycling control built from Button. |

General controls reuse existing containers, surfaces, sprites, labels, and slots. Accordion, cycle selectors, and rich list rows do not require new drawing primitives. Native tooltip rendering and the native font splitter handle platform-specific rendering and text behaviour.

Vanilla's specialised screens, such as inventory, world selection, recipes, and player previews, do not each require a dedicated Compound widget. Their reusable interactions should be expressible through the available composition APIs; specialised rendering can use an appropriate primitive when a concrete consumer requires it. Inventory slots remain integrated with container screens. A dedicated text/link-button API and platform link-opening behaviour are outside the interactions verified here.

## Performance and remaining limits

No frame-time, allocation, or large-list benchmark has been run. Reaching row 100 verifies navigation and rendering, not a performance target.

- Lists and trees still compose their rows eagerly. Virtualization may be warranted for large data sets after measurement.
- Invalidations are coalesced, but dirty layout still measures and places the whole tree once per flush. Local subtree layout remains future work.
- Pointer hover on the repaired controls uses live visual suppliers where practical, reducing structural rebuilds. This is a source-level improvement without measured timing claims.
- Full resource-pack, GUI-scale, Unicode, IME, narration, and combinations of nested overlays have not been exhaustively validated.
- List/tree arrow, Home, and End behaviour still needs a live check through an input path that delivers their actual GLFW key codes.

## Build and test status

The client was built and launched repeatedly for the live checks above, then stopped through Gradle after the final pass. No automated tests were run during the repair pass, as requested. Compilation and successful launch do not establish coverage of every API or interaction.

Earlier review attempts encountered locked build outputs and compilation failures in the headless `BufferedImageScreenContext` test support. A separate temporary build directory allowed production compilation and client launch. Those historical test attempts did not execute a test suite. The approximate headless renderer is not evidence of vanilla appearance.

The recorded gallery pass is complete within the interaction coverage above. Performance claims require a separate measured workload; broader accessibility and navigation-key coverage remain validation limits.

## Cursor composition and animation

TextInput and TextArea compose their caret and selection backgrounds from Rect elements. Text no longer draws selection rectangles or exposes setHighlight; compose a Rect behind text for highlighting. The existing Cursor convenience element remains composed, not primitive.

Both inputs support `setCursorAnimation(600, Easing.EASE_IN_OUT)`; the interval is milliseconds per fade direction. `Easing.STEP` supplies the default on/off blink, and other existing or custom Easing functions work without separate cursor modes. The shared internal CursorBlink uses one scope-owned AnimatedState<Float>, restarts visibly after focus/edit/navigation, and releases it on detach. Runtime timing changes reuse that animation. TextArea's existing setCursorAnimationMode remains a convenience API. The gallery Text tab includes a Blink / Ease in-out selector affecting both its text fields.

Live cursor checks: typed into the single-line field, selected all text, switched the gallery to Ease in/out, typed two lines, and selected both lines. Carets appeared beside the text, selection rectangles followed each line, and eased opacity included an intermediate gray phase. Tabs now let Button play the sole click sound; the duplicate sound in selectTab was removed. Unselected labels use vanilla's lower baseline, while selected labels keep the normal baseline. ScrollArea reserves a four-pixel gutter before its scrollbar.

Final rebuild: inspected the expanded scrollbar gutter and switched Controls to Lists; selected and unselected tab labels follow their distinct vanilla offsets. Client stopped through Gradle. Sound duplication was verified from the playback paths, not an audio capture.

## Adversarial follow-up

Three sub-agents reviewed and cross-checked the fixes. Confirmed defects repaired:

- Accordion and Tabs rebuild only their content when selection changes, preserving header focus.
- CycleButton preserves explicitly supplied content.
- TextArea reveals the focused caret after resizing and selected-text deletion. Ordinary layout passes preserve wheel scrolling. Reducing its maximum length enforces the limit even when a filter rejects the truncated value, preventing a later insertion exception.
- Container slots update from the live tree after layout and before rendering. Transient previews reset each frame. Quick-craft uses vanilla's count calculation and stack limit; a single drag target no longer aborts other slot updates.
- Native container initialization restores item interaction handlers. Composed backgrounds protect carried items from accidental outside-click dropping, with a custom-background hook and vanilla bounds fallback.

Shared invalidation helpers replace repeated attachment checks. Redundant implementation comments and long text-input examples were removed. The final maintainability pass found no further actionable issues in these changes.

The final client compiled and ran at 1440 x 900. Live checks covered repeated Enter activation of the accordion, Tab/Enter navigation between headers, custom cycle-button labels, long-note narrowing and widening with the caret visible, deliberate wheel scrolling, inventory pickup, clicking empty panel background while carrying an item, drag placement, and the native item tooltip. The client was stopped through Gradle.

Evidence: [text reflow](D:/Modding/Minecraft/Molecule/run/screenshots/2026-09-27_02.24.07.png) and [inventory tooltip](D:/Modding/Minecraft/Molecule/run/screenshots/2026-09-27_02.25.45.png). Creative clone previews, maximum-count overlays, dynamic inventory remounting, and the maximum-length/filter combination were checked in source; those cases were not individually reproduced in the game. No automated test suite or performance benchmark was run.

## Scrollbar styles and documentation cleanup

ScrollArea now offers LIST and CREATIVE styles. LIST uses vanilla's six-pixel proportional scrollbar. CREATIVE uses the native 12-by-15 enabled and disabled thumbs inside a 14-pixel recessed track, with colours matched to the creative inventory texture. Both reserve a four-pixel content gutter and compose existing surfaces and sprites. Horizontal scrolling adapts the same styles.

The gallery's Scrollbars tab was checked at 1440 x 900: vertical dragging reached the last row in both styles, wheel scrolling moved the creative viewport, and both horizontal thumbs reached the end of their content. The list scrollbar disappeared when content fit; the creative thumb remained visibly disabled and ignored dragging. A cross-review caught and fixed the creative track's descendant sizing before this pass. [Scrollbar comparison](D:/Modding/Minecraft/Molecule/run/screenshots/2026-09-27_02.37.11.png). Production compilation passed and the client was stopped through Gradle.

Removed 28 superseded widget proposals, implementation plans, and old composition examples. Current API references, project guidance, and these validation notes remain. The composition API reference now documents scrollbar style selection.

Follow-up: renamed CREATIVE to GRIPPY to describe its appearance. The horizontal thumb had been stretched from 12 by 15 to 15 by 12; it now retains its native 12-by-15 dimensions, with a 17-pixel-high horizontal track. Rebuilt and visually verified the corrected proportions beside the vertical thumb in the enlarged gallery.

## Progress rendering

Compared the 26.1.2 vanilla sources for BossHealthOverlay, AbstractFurnaceScreen, and LoadingOverlay. Boss and furnace progress crop textures; the loading screen draws a one-pixel outline and inset solid fill. Compound had squeezed a complete boss-bar texture into the changing fill width. Its general-purpose ProgressBar now uses the loading-screen treatment, composed from Surface and Rect, retaining configurable colours, labels, and the existing indeterminate animation.

This also exposed a Surface bug: a solid backing rectangle used as a border showed through transparent backgrounds. Borders now consist of four non-overlapping edges. In-game validation at 1440 x 900 confirmed the transparent interior, advancing progress from 65% to 70%, and the animated sweep clipping within its inset. [Progress bars](D:/Modding/Minecraft/Molecule/run/screenshots/2026-09-27_02.51.24.png). Production compilation passed; the client was stopped through Gradle.

## Whole-stack repair and Minecraft 26.3 validation

Updated to Minecraft 26.3, NeoForge 26.3.0.23-beta, and ModDevGradle 2.0.147. The new SDL input constants, screen/HUD access, cursors, native tooltip/render extraction, and container previews are adapted. The 19 audit findings and architecture changes are recorded in [compound-ui-audit.md](compound-ui-audit.md).

Ran the client at 1440×900. Windows capture returned a black SDL client area; Minecraft's F2 screenshots showed the actual rendering. Checks confirmed:

- Bound container revision 0→1→2 hides/restores conditional children while the active animation count stays at 2.
- Mounted checkbox label placement, radio removal, progress maximum, divider thickness/colour, and solid toggle thumb update immediately.
- Centered text hit-testing inserts between the clicked characters; supplementary Unicode input/deletion preserves the surrounding text; replacing scrolled text with a short value keeps it visible.
- Horizontal grippy dragging reaches the content end without squashing its native 12×15 thumb. F3+T reload preserves widget/scrollbar textures. Tree expansion/collapse also works.
- The composed crate opens on 26.3, shows native item tooltips, picks up and places stacks, and keeps carried items on panel-background clicks. The same automated drag gives endpoint-only placement in Compound and vanilla; intermediate previews were source-reviewed.

Evidence: [Unicode input](../run/screenshots/2026-09-27_04.05.57.png), [resource reload](../run/screenshots/2026-09-27_04.07.51.png), [inventory tooltip](../run/screenshots/2026-09-27_04.12.36.png), and [carried stack on panel](../run/screenshots/2026-09-27_04.13.30.png). The original demo world was preserved; validation used an upgraded copy. Its vanilla upgrade emitted village-structure conversion diagnostics, and Windows performance-counter diagnostics appeared at startup. No claim of a clean vanilla-world migration is made.

The client was stopped through Gradle. No branch was published and no PR was opened.
Existing checks: five Compound tests and four demo harness tests pass. Updated stale layout assertions to check authored content, gutter and clipping; repaired the existing headless harness runtime and font collision. The Java2D images are diagnostic approximations, not visual evidence of vanilla appearance. Rechecked 26.3 LoadingOverlay: the one-pixel outline and inset solid fill remain native behavior.

## Second review pass — 2026-09-27

Additional component/core/standards reviews and cross-reviews repaired lifecycle, layout arithmetic, scroll propagation, popup sizing, editing and native input/tooltip issues. Details and API migration notes are in [the audit](compound-ui-audit.md#second-adversarial-pass).

Live checks at 1440×900 confirmed fixed modal footer positioning during scrolling, selected-option visibility on opening a dropdown, horizontal wheel movement with the native-sized grippy thumb, and multiline paste/wrapping. Other editing edge cases remain explicitly source-reviewed. The client was stopped through Gradle; nothing was published.
