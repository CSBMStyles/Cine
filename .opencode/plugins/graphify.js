// graphify OpenCode plugin (V2 + V1 compatible)
// Injects a knowledge graph reminder before shell/bash tool calls when the graph exists.
import { existsSync } from "fs";
import { join } from "path";

const REMINDER =
  'echo "[graphify] Knowledge graph available. Read graphify-out/GRAPH_REPORT.md for god nodes and architecture context before searching files." && ';

export default {
  id: "graphify",
  async setup(ctx) {
    let reminded = false;
    const directory = ctx.location.directory;

    await ctx.shell.hook("create.before", (event) => {
      if (reminded) return;
      if (!existsSync(join(directory, "graphify-out", "graph.json"))) return;

      event.command = REMINDER + event.command;
      reminded = true;
    });
  },
  async server({ directory }) {
    let reminded = false;

    return {
      "tool.execute.before": async (input, output) => {
        if (reminded) return;
        if (!existsSync(join(directory, "graphify-out", "graph.json"))) return;

        if (input.tool === "bash") {
          output.args.command = REMINDER + output.args.command;
          reminded = true;
        }
      },
    };
  },
};
