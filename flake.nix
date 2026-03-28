{
  description = "microapis-examples - TOTP microservice";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    flake-parts.url = "github:hercules-ci/flake-parts";
  };

  outputs =
    inputs:
    inputs.flake-parts.lib.mkFlake { inherit inputs; } {
      systems = [
        "x86_64-linux"
        "aarch64-darwin"
        "x86_64-darwin"
      ];

      perSystem =
        { pkgs, ... }:
        {
          devShells.default = pkgs.mkShell {
            packages = with pkgs; [
              awscli2
              chezmoi
              coursier
              direnv
              flyctl
              fzf
              gh
              gnupg
              jdk21
              lazygit
              mise
              neovim
              openssl
              ripgrep
              sbt
              starship
              watchman
              zsh
            ];

            shellHook = ''
              export PROJECT_ROOT="$(git rev-parse --show-toplevel)"
              export JAVA_HOME="${pkgs.jdk21}/lib/openjdk"

              if [ -f "$PROJECT_ROOT/.env" ]; then
                set -a
                . "$PROJECT_ROOT/.env"
                set +a
              fi

              echo "microapis-examples dev environment ready"
              echo ""
              echo "Commands:"
              echo "  sbt test"
              echo "  sbt pack"
              echo "  fly deploy"
            '';
          };
        };
    };
}
