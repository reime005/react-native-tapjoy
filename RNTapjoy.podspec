require "json"

package = JSON.parse(File.read(File.join(__dir__, 'package.json')))

version = package['version']

source = { :git => 'https://github.com/reime005/react-native-tapjoy.git' }

if version == '1000.0.0'
  # This is an unpublished version, use the latest commit hash of the react-native repo, which we’re presumably in.
  source[:commit] = `git rev-parse HEAD`.strip
else
  source[:tag] = "v#{version}"
end

Pod::Spec.new do |s|
  s.name         = "RNTapjoy"
  s.version      = version
  s.license      = package["license"]
  s.homepage     = package["homepage"]
  s.authors      = package["author"]
  s.summary      = package["description"]
  s.description      = package["description"]
  s.source       = source
  s.source_files  = "ios/*.{m,h}"
  s.public_header_files = "ios/*.h"
  s.preserve_paths = 'README.md', 'package.json', '*.js'

  s.platform     = :ios, "9.0"
  s.dependency 'React'
  s.dependency 'TapjoySDK', '12.3.1'

  s.subspec "RCT" do |ss|
    ss.source_files = "ios/RCT/**/*.{h,m}"
  end

  s.subspec "RN" do |ss|
    ss.source_files = "ios/RN/**/*.{h,m}"
  end

  s.xcconfig = {
    'HEADER_SEARCH_PATHS' => [
        "$(inherited)",
        "${SRCROOT}/../../React/**",
        "${SRCROOT}/../../node_modules/react-native/**"
      ].join(' ')
  }
end
