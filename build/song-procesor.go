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

type ChordIndex struct {
    Name string			`json:"name"`
    Key string			`json:"key"`
    String []string		`json:"strings"`
    Fingers []string	`json:"fingers"`
}

func processSongs() {
	var index []SongIndex
	dir := "src/songs/"
	dirJson := "output/json/"
	dirHtml := "output/html/"

	// first cleanup
	os.RemoveAll(dirJson)
	os.RemoveAll(dirHtml)

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
			song := songParser.ParseSong(string(b))

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
			
			b, err = json.Marshal(song)
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

	b, err := json.Marshal(index)
	if (err != nil) {
		fmt.Println("problem when writing index json file", err)
		return
	}

	err = WriteStringToFile(dirJson + "/index.json", string(b))	
}

func processChords() {
	var index []ChordIndex
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

			b, err = json.Marshal(chord)
			if (err != nil) {
				fmt.Println("problem when writing html file", file, err)
				continue
			}
			
			err = WriteStringToFile(jsonOutputPath, string(b))	
			if (err != nil) {
				fmt.Println("problem when writing json file", file, err)
				continue
			}

			index = append(index, ChordIndex{chord.Name, chord.Key, chord.Strings, chord.Fingers})
		}
	}

	b, err := json.Marshal(index)
	if (err != nil) {
		fmt.Println("problem when writing index json file", err)
		return
	}

	err = WriteStringToFile(dirJson + "/index.json", string(b))	
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

func main() {
	processSongs()
	processChords()
}