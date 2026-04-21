<configuration>
    <project_profile>
        <type>Library Collection (Minecraft Development)</type>
        <identity_constraints>
            Compound is NOT a Minecraft mod. It is a set of libraries assisting mod development.
            It acts purely as a dependency; it has no mod class, no entrypoint, and no event listeners.
            Any attempt to load it as a standalone mod will fail.
        </identity_constraints>
    </project_profile>

    <coding_standards>
        <java_modernity>
            Use modern Java features. Avoid legacy constructs.
            - Use enhanced `switch` expressions (avoid old switch statements).
            - Use `var` for local variable type inference.
            - Use `records` for data carriers.
        </java_modernity>

        <imports>
            Always import classes at the top of the file.
            NEVER use fully qualified names inline.
            - BAD: `com.tridevmc.compound.ui.compose.layout.Bounds b = ...`
            - GOOD: `import com.tridevmc.compound.ui.compose.layout.Bounds; ... Bounds b = ...`
        </imports>

        <style>
            Follow the existing code style and conventions found in the codebase.
            Consistency is the priority.
        </style>
    </coding_standards>

    <documentation_guidelines>
        <javadoc>
            Required for all public classes, methods, and fields.
            Private/package-private members require comments only if their purpose is obscure.
        </javadoc>
        <inline_comments>
            Explain the "Why", not the "What".
            The code itself should explain what is being done. Avoid stating the obvious.
        </inline_comments>
    </documentation_guidelines>

    <environment>
        <build_tool>Use `gradle` directly. Do NOT use `gradlew`.</build_tool>
        <reference_path>
            When asked to inspect the Minecraft codebase, look in:
            `build\moddev\artifacts` (Find the LATEST deobfuscated jar).
        </reference_path>
    </environment>
</configuration>