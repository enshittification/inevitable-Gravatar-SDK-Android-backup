#!/bin/bash -eu

echo "--- :rubygems: Setting up Gems"
install_gems

echo "--- :hammer_and_wrench: Building"
bundle exec fastlane build_and_upload_prototype_build
