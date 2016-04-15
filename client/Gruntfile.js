'use strict';
module.exports = function(grunt) {

    // Project configuration.
    grunt.initConfig({
        // Metadata.
        pkg: grunt.file.readJSON('package.json'),
        banner: '/*! <%= pkg.name %> - v<%= pkg.version %> - ' +
            '<%= grunt.template.today("yyyy-mm-dd") %>\n' +
            '<%= pkg.homepage ? "* " + pkg.homepage + "\\n" : "" %>' +
            '* Copyright (c) <%= grunt.template.today("yyyy") %> <%= pkg.author.name %>;' +
            ' Licensed <%= _.pluck(pkg.licenses, "type").join(", ") %> */\n',
        // Task configuration.
        concat: {
            options: {
                banner: '<%= banner %>'
            },
            dist: {
                nonull: true,
                src: [
                    'src/main.js',
                    'src/types/*.js',
                    'src/directives/*.js',
                    'src/services/*.js',
                    'src/app/**/*.js',
                    '!src/app/**/*test.js'
                ],
                dest: 'dist/<%= pkg.name %>.js'
            }
        },
        jsdoc: {
            dist: {
                src: [
                    'dist/*.js',
                    'README.md'
                ],
                options: {
                    destination: 'documentation',
                    configure: 'jsdoc.conf.json'
                }
            }
        },
        uglify: {
            options: {
                banner: '<%= banner %>',
                preserveComments: false,
                mangle: {
                    except: ['jQuery']
                }
            },
            dist: {
                src: '<%= concat.dist.dest %>',
                dest: 'dist/<%= pkg.name %>.min.js'
            }
        },
        nodeunit: {
            files: ['test/**/*_test.js']
        },
        jshint: {
            options: {
                jshintrc: '.jshintrc'
            },
            gruntfile: {
                src: 'Gruntfile.js'
            },
            src: {
                options: {
                    jshintrc: 'src/.jshintrc'
                },
                src: ['src/**/*.js']
            }
        },
        copy: {
            main: {
                files: [{
                    expand: true,
                    cwd: 'src/app',
                    src: ['**/*.html'],
                    dest: 'templates/',
                    filter: 'isFile'
                }]
            },
            dtemplates: {
                files: [{
                    expand: true,
                    cwd: 'src/directives',
                    src: ['**/*.html'],
                    dest: 'templates/directives',
                    filter: 'isFile'
                }]
            }
        },
        jscs: {
            src: 'src/**/*.js',
            options: {
                preset: 'google.json',
                config: '.jscsrc',
                // If you use ES6 http://jscs.info/overview.html#esnext
                esnext: true,
                // If you need output with rule names
                // http://jscs.info/overview.html#verbose
                verbose: true,
                // Autofix code style violations when possible.
                fix: true,
                requireCurlyBraces: ["if"]
            }
        },
        watch: {
            lib: {
                files: [
                    'src/main.js',
                    'src/types/*.js',
                    'src/directives/*.js',
                    'src/services/*.js',
                    'src/app/**/*.js',
                    'src/app/**/*.html',
                    '!src/app/**/*test.js'
                ],
                tasks: ['default']
            },
            withdocs: {
                files: [
                    'README.md',
                    'src/main.js',
                    'src/types/*.js',
                    'src/directives/*.js',
                    'src/services/*.js',
                    'src/app/**/*.js',
                    'src/app/**/*.html',
                    '!src/app/**/*test.js'
                ],
                tasks: ['build']
            },
            gruntfile: {
                files: 'Gruntfile.js',
                tasks: ['default']
            }
        }
    });
    // These plugins provide necessary tasks.
    grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.loadNpmTasks('grunt-contrib-nodeunit');
    grunt.loadNpmTasks('grunt-contrib-jshint');
    grunt.loadNpmTasks('grunt-jscs');
    grunt.loadNpmTasks('grunt-contrib-watch');
    grunt.loadNpmTasks('grunt-jsdoc');
    grunt.loadNpmTasks('grunt-contrib-copy');

    // Default task.
    grunt.registerTask('default', ['copy', 'jscs', 'jshint', 'concat', 'uglify']);
    grunt.registerTask('build', ['copy', 'jscs', 'jshint', 'concat', 'uglify', 'update-docs']);
    grunt.registerTask('update-docs', ['concat', 'jsdoc']);
};