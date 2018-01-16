package main

import (
	"fmt"
	"os"
	"io/ioutil"
	"path/filepath"
    "io"
    "strings"
	"encoding/json"
	chordParser "./chord"
	songParser "./song"
)

type SongIndex struct {
    Name string		`json:"name"`
    Artist string	`json:"artist"`
    Path string		`json:"path"`
}

func cleanup() (string, string) {
	dirJson := "output/json/"
	dirHtml := "output/html/"

	// first cleanup
	os.RemoveAll(dirJson)
	os.RemoveAll(dirHtml)

	return dirJson, dirHtml
}

func processSongs(chords* []chordParser.Chord, dirJson string, dirHtml string) {
	var index []SongIndex
	dir := "src/songs/"

	fileList := make([]string, 0)
	e := filepath.Walk(dir, func(path string, f os.FileInfo, err error) error {
		fileList = append(fileList, path)
		return err
	})
	
	if e != nil {
		panic(e)
	}

	for _, file := range fileList {
		if (filepath.Ext(file) == ".song") {

			b, err := ioutil.ReadFile(file)
			if (err != nil) {
				fmt.Println("problem when reading " + file)
			}
			song := songParser.ParseSong(string(b), chords)

			outputPath := string(file)
			outputPath = outputPath[(len(dir)):]
			outputPath = outputPath[:(len(outputPath) - len(filepath.Ext(file)))]
			
			htmlOutputPath := dirHtml + "songs/" + outputPath + ".html"
			jsonOutputPath := dirJson + "songs/" + outputPath + ".json"

			err = WriteStringToFile(htmlOutputPath, song.Html)
			if (err != nil) {
				fmt.Println("problem when writing file", file, err)
				continue
			}
			
			b, err = toJson(song)
			if (err != nil) {
				fmt.Println("problem when writing html file", file, err)
				continue
			}
			
			err = WriteStringToFile(jsonOutputPath, string(b))	
			if (err != nil) {
				fmt.Println("problem when writing json file", file, err)
				continue
			}

			index = append(index, SongIndex{song.Name, song.Artist, outputPath})
		}
	}

	b, err := toJson(index)
	if (err != nil) {
		fmt.Println("problem when writing index json file", err)
		return
	}

	err = WriteStringToFile(dirJson + "songs/index.json", string(b))	
}

func processChords() []chordParser.Chord {
	var chords []chordParser.Chord
	dir := "src/chords"
	dirJson := "output/json/chords"

	fileList := make([]string, 0)
	e := filepath.Walk(dir, func(path string, f os.FileInfo, err error) error {
		fileList = append(fileList, path)
		return err
	})
	
	if e != nil {
		panic(e)
	}

	for _, file := range fileList {
		if (filepath.Ext(file) == ".chord") {

			b, err := ioutil.ReadFile(file)
			if (err != nil) {
				fmt.Println("problem when reading " + file)
			}
			chord := chordParser.ParseChord(string(b))

			outputPath := string(file)
			outputPath = outputPath[(len(dir)):]
			outputPath = outputPath[:(len(outputPath) - len(filepath.Ext(file)))]
			
			jsonOutputPath := dirJson + outputPath + ".json"

			b, err = toJson(chord)
			if (err != nil) {
				fmt.Println("problem when writing html file", file, err)
				continue
			}

			fmt.Println("output", jsonOutputPath)
			
			err = WriteStringToFile(jsonOutputPath, string(b))	
			if (err != nil) {
				fmt.Println("problem when writing json file", file, err)
				continue
			}

			chords = append(chords, *chord)
		}
	}

	b, err := toJson(chords)
	if (err != nil) {
		fmt.Println("problem when writing index json file", err)
		return chords
	}

	err = WriteStringToFile(dirJson + "/index.json", string(b))	
	return chords
}

func WriteStringToFile(file string, s string) error {
    os.MkdirAll(filepath.Dir(file), os.ModePerm);
    fo, err := os.Create(file)
    if err != nil {
        return err
    }
    defer fo.Close()
    _, err = io.Copy(fo, strings.NewReader(s))
    if err != nil {
        return err
    }

    return nil
}

func toJson(data interface{}) ([]byte, error) {
	return json.MarshalIndent(data, "", "  ");
}

func main() {
	dirJson, dirHtml := cleanup()
	chords:= processChords()
	processSongs(&chords, dirJson, dirHtml)
}